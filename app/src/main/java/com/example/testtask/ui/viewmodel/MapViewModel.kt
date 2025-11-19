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
class MapViewModel @Inject constructor(
    private val repo: LocationRepository
) : ViewModel() {

    val history = MutableLiveData<List<Location>>()

    fun loadHistory(userId: String) {
        viewModelScope.launch {
            history.postValue(repo.getLocations(userId))
        }
    }
}
