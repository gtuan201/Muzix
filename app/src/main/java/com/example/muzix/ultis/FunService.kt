package com.example.muzix.ultis

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.muzix.service.PlayMusicService
import com.example.muzix.ultis.Constants.Companion.UPDATE_STATUS_PLAYING_NOTIFICATION

fun sendActionToService(action: Int,context: Context,activity: Activity) {
    val intent = Intent(context, PlayMusicService::class.java)
    intent.putExtra(UPDATE_STATUS_PLAYING_NOTIFICATION, action)
    activity.startService(intent)
}