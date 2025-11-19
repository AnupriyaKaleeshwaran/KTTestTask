package com.example.testtask.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testtask.databinding.ActivityMapBinding
import com.example.testtask.ui.viewmodel.MapViewModel
import com.example.testtask.util.SessionManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import kotlinx.coroutines.*
import com.example.testtask.data.model.Location
import com.example.testtask.ui.adapter.CustomInfoWindowAdapter

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private val vm: MapViewModel by viewModels()
    private var playbackJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment =
            supportFragmentManager.findFragmentById(com.example.testtask.R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

        // This is for from mainactiitvy recycler
        val lat = intent.getDoubleExtra("lat", -1.0)
        val lng = intent.getDoubleExtra("lng", -1.0)

        if (lat != -1.0 && lng != -1.0) {
            showSingleLocation(lat, lng)
            setupEmptyButtons()
            return
        }

        // This is for from mainactiitvy playbackbutton
        val userId = SessionManager(this).getUser()!!

        vm.loadHistory(userId)

        vm.history.observe(this) { list ->
            if (list.isNotEmpty()) {
                showLastLocationBeforePlayback(list)
                setupPlaybackButtons(list)
            }
        }
    }


    private fun showSingleLocation(lat: Double, lng: Double) {
        val pos = LatLng(lat, lng)

        map.addMarker(
            MarkerOptions()
                .position(pos)
                .title("Lat: $lat, Lng: $lng")
                .snippet("Selected Location")
        )?.showInfoWindow()

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17f))
    }

    private fun setupEmptyButtons() {
        binding.btnPlay.setOnClickListener { }
        binding.btnStop.setOnClickListener { }
    }


    private fun setupPlaybackButtons(list: List<Location>) {
        binding.btnPlay.setOnClickListener {
            playbackJob?.cancel()
            startPlayback(list)
        }

        binding.btnStop.setOnClickListener {
            playbackJob?.cancel()
        }
    }

    private fun startPlayback(points: List<Location>) {
        playbackJob?.cancel()

        playbackJob = CoroutineScope(Dispatchers.Main).launch {
            for (loc in points) {
                val pos = LatLng(loc.latitude, loc.longitude)
                map.clear()

                map.addMarker(
                    MarkerOptions()
                        .position(pos)
                        .title("Lat: ${loc.latitude}, Lng: ${loc.longitude}")
                        .snippet("Time: ${loc.timestamp}")
                )?.showInfoWindow()

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18f))
                delay(1200)
            }
        }
    }
    private fun showLastLocationBeforePlayback(list: List<Location>) {
        val last = list.last()

        val pos = LatLng(last.latitude, last.longitude)

        map.clear()
        map.addMarker(
            MarkerOptions()
                .position(pos)
                .title("Last Location")
                .snippet("Lat: ${last.latitude}, Lng: ${last.longitude}")
        )?.showInfoWindow()

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18f))
    }

}
