package com.example.testtask.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.testtask.R
import com.example.testtask.databinding.ActivitySplashBinding
import com.example.testtask.ui.auth.LoginActivity
import com.example.testtask.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val splashScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        splashScope.launch {
            delay(3_000)
            startActivity(Intent(this@Splash, LoginActivity::class.java))
            finish()
        }
    }
}