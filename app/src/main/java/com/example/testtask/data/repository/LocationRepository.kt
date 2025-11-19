package com.example.testtask.data.repository

import com.example.testtask.data.model.Location
import com.example.testtask.util.RealmManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val realmManager: RealmManager
) {

    suspend fun saveLocation(userId: String, lat: Double, lng: Double) =
        withContext(Dispatchers.IO) {

            val entity = Location().apply {
                this.userId = userId
                this.latitude = lat
                this.longitude = lng
                this.timestamp = System.currentTimeMillis()
            }

            realmManager.saveLocation(entity)
        }

    suspend fun getLocations(userId: String): List<Location> =
        withContext(Dispatchers.IO) {
            realmManager.getLocationsForUser(userId)
        }
}
