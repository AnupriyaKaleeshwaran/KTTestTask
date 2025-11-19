package com.example.testtask.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtask.data.model.User
import com.example.testtask.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun login(email: String) {
        viewModelScope.launch {
            val u = repo.login(email)
            _user.postValue(u)
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            val list = repo.getAllUsers()
            _users.postValue(list)

            list.forEach { user ->
                Log.e("UserDetails", "User: ${user.email} | id: ${user.id}")
            }

        }
    }
}
