package com.example.testtask.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testtask.databinding.ActivityMainBinding
import com.example.testtask.ui.adapter.LocationAdapter
import com.example.testtask.ui.map.MapActivity
import com.example.testtask.ui.viewmodel.MainViewModel
import com.example.testtask.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private lateinit var adapter: LocationAdapter
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        val userId = session.getUser() ?: return

        setLocationAdapter()

        vm.locations.observe(this) { list ->
            Log.e("list", list.toString())
            adapter.submitList(list)
        }

        vm.load(userId)

        binding.btnOpenMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setLocationAdapter() {
        adapter = LocationAdapter { item ->
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("lat", item.latitude)
            intent.putExtra("lng", item.longitude)
            intent.putExtra("time", item.timestamp)
            startActivity(intent)
        }

        binding.rvLocations.layoutManager = LinearLayoutManager(this)
        binding.rvLocations.adapter = adapter
    }
}
