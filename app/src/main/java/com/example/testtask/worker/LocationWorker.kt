package com.example.testtask.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.testtask.data.model.User
import com.example.testtask.data.model.Location as RealmLocation
import com.example.testtask.data.repository.LocationRepository
import com.example.testtask.util.RealmManager
import com.google.android.gms.location.*
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val realm: Realm by lazy {
        val config = RealmConfiguration.Builder(
            schema = setOf(User::class, RealmLocation::class)
        )
            .name("location.realm")
            .build()

        Realm.open(config)
    }

    private val realmManager: RealmManager by lazy {
        RealmManager(realm)
    }

    private val repo: LocationRepository by lazy {
        LocationRepository(realmManager)
    }

    override suspend fun doWork(): Result {
        val userId = inputData.getString("user_id") ?: return Result.failure()

        if (!hasPermission()) return Result.retry()

        val fused = LocationServices.getFusedLocationProviderClient(applicationContext)
        val loc = getLocation(fused) ?: return Result.retry()

        Log.e("locations", loc.latitude.toString())
        Log.e("locations2", loc.longitude.toString())


        repo.saveLocation(userId, loc.latitude, loc.longitude)

        return Result.success()
    }

    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun getLocation(fused: FusedLocationProviderClient): Location? {
        if (!hasPermission()) return null

        val fresh = suspendCancellableCoroutine<Location?> { cont ->
            try {
                fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resume(null) }
            } catch (_: SecurityException) {
                cont.resume(null)
            }
        }

        if (fresh != null) return fresh

        val last = suspendCancellableCoroutine<Location?> { cont ->
            try {
                fused.lastLocation
                    .addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resume(null) }
            } catch (_: SecurityException) {
                cont.resume(null)
            }
        }

        if (last != null) return last

        return singleUpdate(fused)
    }

    private suspend fun singleUpdate(fused: FusedLocationProviderClient): Location? {
        if (!hasPermission()) return null

        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setMaxUpdates(1)
            .build()

        return suspendCancellableCoroutine { cont ->
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fused.removeLocationUpdates(this)
                    cont.resume(result.lastLocation)
                }
            }

            try {
                fused.requestLocationUpdates(req, callback, Looper.getMainLooper())
            } catch (_: SecurityException) {
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            cont.invokeOnCancellation {
                fused.removeLocationUpdates(callback)
            }
        }
    }
}
