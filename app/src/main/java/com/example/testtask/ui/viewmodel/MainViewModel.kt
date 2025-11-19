package com.example.testtask.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtask.data.model.Location
import com.example.testtask.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: LocationRepository
) : ViewModel() {

    val locations = MutableLiveData<List<Location>>()

    fun load(userId: String) {
        viewModelScope.launch {
            locations.postValue(repo.getLocations(userId))
        }
    }
}
