package com.example.lesson1

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.lang.reflect.Field
class MusicPlayerService : Service() {

    private val NOTIFICATION_ID = 1
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false
    private val songsList = ArrayList<String>()
    private var soundId = 0
    override fun onCreate() {
        super.onCreate()
        listRaw()
        initPlayer(soundId)
    }
    private fun initPlayer(id: Int){
        val resId = resources.getIdentifier(songsList[id], "raw", packageName)
        mediaPlayer = MediaPlayer.create(this, resId)
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            Constants.ACTION.STARTFOREGROUND_ACTION -> {
                startForeground(NOTIFICATION_ID, createNotification())
                playMusic()
                isPlaying = true
            }
            Constants.ACTION.STOPFOREGROUND_ACTION -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopMusic()
                isPlaying = false
                stopSelf()
            }
            Constants.ACTION.NEXTSONG_ACTION -> {
                stopForeground(STOP_FOREGROUND_DETACH)
                stopMusic()

                startForeground(NOTIFICATION_ID, createNotification())
                playNext()
                isPlaying = true
            }
            Constants.ACTION.PREVSONG_ACTION -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopMusic()

                startForeground(NOTIFICATION_ID, createNotification())
                playPrev()
                isPlaying = true
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlaying) {
            stopMusic()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun listRaw() {
        val fields: Array<Field> = R.raw::class.java.fields
        for (count in fields.indices) {
            songsList.add(fields[count].name)
        }
    }
    private fun createNotification(): Notification {
        val stopIntent = Intent(this, MusicPlayerService::class.java)
        stopIntent.action = Constants.ACTION.STOPFOREGROUND_ACTION
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent,
            PendingIntent.FLAG_MUTABLE)
        val playIntent = Intent(this, MusicPlayerService::class.java)
        playIntent.action = Constants.ACTION.STARTFOREGROUND_ACTION
        val pendingStartIntent = PendingIntent.getService(this, 0, playIntent,
            PendingIntent.FLAG_MUTABLE)
        val nextIntent = Intent(this, MusicPlayerService::class.java)
        nextIntent.action = Constants.ACTION.NEXTSONG_ACTION
        val pendingNextIntent = PendingIntent.getService(this, 0, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        val prevIntent = Intent(this, MusicPlayerService::class.java)
        prevIntent.action = Constants.ACTION.PREVSONG_ACTION
        val pendingPrevIntent = PendingIntent.getService(this, 0, prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val builder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing Music")
            .setSmallIcon(R.drawable.music)
            .addAction(R.drawable.ic_launcher_foreground,"prev", pendingPrevIntent)
            .addAction(R.drawable.play, "start", pendingStartIntent)
            .addAction(R.drawable.pause, "pause",  pendingStopIntent)
            .addAction(R.drawable.skip, "next", pendingNextIntent)

        return builder.build()
    }

    private fun playMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
    }
    private fun playNext() {
        if (soundId < songsList.size - 1) {
            soundId++
        } else {
            soundId = 0
        }
        initPlayer(soundId)
        mediaPlayer.start()
    }
    private fun playPrev() {
        if (soundId <= 0) {
            soundId = songsList.size - 1
        } else{
            soundId--
        }
        initPlayer(soundId)
        mediaPlayer.start()
    }
}