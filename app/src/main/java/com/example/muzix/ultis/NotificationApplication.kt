package com.example.muzix.ultis

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log

class NotificationApplication : Application() {
    companion object {
        const val NOTIFICATION_CHANNEL = "channel_play"
        const val NOTIFICATION_DOWNLOAD = "channel_download"
    }
    override fun onCreate() {
        super.onCreate()
        createChanelNotification()
    }
    private fun createChanelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(NOTIFICATION_CHANNEL,"MusicNotification", NotificationManager.IMPORTANCE_LOW)
            channel.setSound(null,null)
            val notificationManager : NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            val channel2 = NotificationChannel(NOTIFICATION_CHANNEL,"DownloadNotification", NotificationManager.IMPORTANCE_LOW)
            channel.setSound(null,null)
            notificationManager.createNotificationChannel(channel2)
        }
    }
}