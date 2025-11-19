package com.example.testtask.ui.auth

import UserAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.testtask.databinding.ActivityLoginBinding
import com.example.testtask.ui.main.MainActivity
import com.example.testtask.ui.viewmodel.AuthViewModel
import com.example.testtask.util.SessionManager
import com.example.testtask.worker.LocationWorker
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val vm: AuthViewModel by viewModels()

    private lateinit var session: SessionManager
    private lateinit var adapter: UserAdapter

    private var workerScheduled = false
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val fine = perms[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarse = perms[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fine || coarse) {
                checkBackgroundPermission {
                    /*session.getUser()?.let { userId ->
                        scheduleWorker(userId) {

                        }
                    }*/
                }
            } else {
                showPermissionDeniedDialog()
            }
        }

    private val backgroundPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                /*session.getUser()?.let { userId ->
                    scheduleWorker(userId) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }*/
            } else {
                showPermissionDeniedDialog()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        setRecyclerView()

        vm.users.observe(this) { list ->
            adapter.submitList(list)
        }

        vm.loadAllUsers()
        requestLocationPermissions {
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            vm.login(email)
        }

        vm.user.observe(this) { user ->
            if (user != null) {
                session.saveUser(user.id)
                scheduleWorker(user.id)
                /*requestLocationPermissions {
                    scheduleWorker(user.id)
                }*/
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter { user ->
            vm.login(user.email)
        }
        binding.rvUsers.adapter = adapter
    }


    private fun requestLocationPermissions(onGranted: () -> Unit) {

        val hasFine = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            checkBackgroundPermission(onGranted)
            return
        }

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }


    private fun checkBackgroundPermission(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasBg = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasBg) {
                backgroundPermissionLauncher.launch(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                onGranted()
            }
        } else {
            onGranted()
        }
    }


    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions required")
            .setMessage("Location permissions are required to track background location.")
            .setPositiveButton("OK") { d, _ -> d.dismiss() }
            .show()
    }


    private fun scheduleWorker(userId: String) {


        val workManager = WorkManager.getInstance(applicationContext)
        val data = workDataOf("user_id" to userId)

        // first immediate entry to check if i can able to start workmanger or not
        val oneTimeReq = OneTimeWorkRequestBuilder<LocationWorker>()
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            "loc_once_work",
            ExistingWorkPolicy.REPLACE,
            oneTimeReq
        )

        workManager.getWorkInfoByIdLiveData(oneTimeReq.id).observe(this) { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        // every 15 minutes
        val periodicReq = PeriodicWorkRequestBuilder<LocationWorker>(
            15, TimeUnit.MINUTES
        ).setInputData(data).build()

        workManager.enqueueUniquePeriodicWork(
            "loc_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicReq
        )
    }


}
