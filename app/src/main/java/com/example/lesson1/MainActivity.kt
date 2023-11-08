package com.example.lesson1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lesson1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playBtn.setOnClickListener {
            val serviceIntent = Intent(this, MusicPlayerService::class.java)
            serviceIntent.action = Constants.ACTION.STARTFOREGROUND_ACTION
            startService(serviceIntent)
        }

        binding.pauseBtn.setOnClickListener {
            val serviceIntent = Intent(this, MusicPlayerService::class.java)
            serviceIntent.action = Constants.ACTION.STOPFOREGROUND_ACTION
            startService(serviceIntent)
        }
        binding.nextBtn.setOnClickListener {
            val serviceIntent = Intent(this, MusicPlayerService::class.java)
            serviceIntent.action = Constants.ACTION.NEXTSONG_ACTION
            startService(serviceIntent)
        }
        binding.prevBtn.setOnClickListener {
            val serviceIntent = Intent(this, MusicPlayerService::class.java)
            serviceIntent.action = Constants.ACTION.PREVSONG_ACTION
            startService(serviceIntent)
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                "Music Player1",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }
}