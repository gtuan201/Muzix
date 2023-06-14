package com.example.muzix.service

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.muzix.R
import com.example.muzix.ultis.NotificationApplication.Companion.NOTIFICATION_MESSAGE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


@SuppressLint("MissingFirebaseInstanceTokenRefresh,MissingPermission")
class FirebaseCloudMessage : FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification!!.title
        val body = message.notification!!.body
        val notification = NotificationCompat.Builder(this,NOTIFICATION_MESSAGE)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.baseline_music_note_24)
        NotificationManagerCompat.from(this).notify(1, notification.build())
        super.onMessageReceived(message)
    }
}