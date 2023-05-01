package com.example.muzix.ultis

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log

class NotificationApplication : Application() {
    companion object {
        const val NOTIFICATION_CHANNEL = "channel_play"
    }
    override fun onCreate() {
        super.onCreate()
        createChanelNotification()
    }
    private fun createChanelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(NOTIFICATION_CHANNEL,"MusicNotification",
                NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(null,null)
            val notificationManager : NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}