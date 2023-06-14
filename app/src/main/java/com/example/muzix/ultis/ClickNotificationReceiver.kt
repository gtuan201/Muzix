package com.example.muzix.ultis

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.muzix.ultis.Constants.Companion.CLICK_NOTIFICATION
import com.example.muzix.view.main.MainActivity

class ClickNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getStringExtra("id_playlist")
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(201)
        if (!id.isNullOrEmpty()){
            val intentNoti = Intent(context,MainActivity::class.java)
            intentNoti.putExtra("id",id)
            intentNoti.action = CLICK_NOTIFICATION
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentNoti)
        }
    }
}