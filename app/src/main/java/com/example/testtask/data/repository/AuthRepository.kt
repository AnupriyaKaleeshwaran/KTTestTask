package com.example.testtask.data.repository

import android.util.Log
import com.example.testtask.data.model.User
import com.example.testtask.util.RealmManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class AuthRepository @Inject constructor(private val realmManager: RealmManager) {

    suspend fun login(email: String): User {
        val found = realmManager.getUser(email)
        if (found != null) return found

        val newUser = User().apply {
            id = UUID.randomUUID().toString()
            this.email = email
        }
        realmManager.saveUser(newUser)
        return newUser
    }

    fun getAllUsers(): List<User> {
        return realmManager.getAllUsers()
    }
}

