package com.example.testtask.util

import com.example.testtask.data.model.Location
import com.example.testtask.data.model.User
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import javax.inject.Inject

class RealmManager @Inject constructor(private val realm: Realm) {

    suspend fun saveUser(user: User) {
        realm.write {
            copyToRealm(user)
        }
    }

    suspend fun saveLocation(location: Location) {
        realm.write {
            copyToRealm(location)
        }
    }

    fun getUser(email: String): User? =
        realm.query<User>("email == $0", email).first().find()

    fun getAllUsers(): List<User> {
        return realm.query<User>().find().toList()
    }

    fun getLocationsForUser(userId: String): List<Location> =
        realm.query<Location>("userId == $0", userId)
            .sort("timestamp")
            .find()
            .toList()
}
