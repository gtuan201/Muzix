package com.example.muzix.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.muzix.R
import com.example.muzix.data.local.AppDatabase
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Notification
import com.example.muzix.model.Playlist
import com.example.muzix.ultis.ClickNotificationReceiver
import com.example.muzix.ultis.NotificationApplication.Companion.NOTIFICATION_CHANNEL
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random


@SuppressLint("MissingFirebaseInstanceTokenRefresh,MissingPermission")
class FirebaseCloudMessage : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification!!.title
        val body = message.notification!!.body
        val data = message.data
        saveNotification(data)
        sendNotification(title, body, data["image"],data["id_playlist"])
        super.onMessageReceived(message)
    }

    @SuppressLint("UnspecifiedImmutableFlag", "LaunchActivityFromNotification")
    private fun sendNotification(title: String?, body: String?, s: String?, id: String?) {
        val intent = Intent(this,ClickNotificationReceiver::class.java)
        intent.putExtra("id_playlist",id)
        val pendingIntent = PendingIntent.getBroadcast(this,101,intent,PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
        Glide.with(this)
            .asBitmap()
            .load(s)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    notification
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSmallIcon(R.drawable.spotify_icon)
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(resource)
                    NotificationManagerCompat.from(this@FirebaseCloudMessage).notify(201, notification.build())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    //Lỗi load ảnh
                    notification
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSmallIcon(R.drawable.spotify_icon)
                        .setContentIntent(pendingIntent)
                    NotificationManagerCompat.from(this@FirebaseCloudMessage).notify(201, notification.build())
                }

            })
    }
    private fun saveNotification(data: Map<String, String>) {
        val title = data["title"]
        val body = data["body"]
        val image = data["image"]
        val id = data["id_playlist"]
        val notification = Notification(null, title, body, image,id)
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.createDatabase(applicationContext).getDao()
            dao.insertNotification(notification)
        }
    }
}