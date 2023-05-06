package com.example.muzix.ultis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.muzix.service.PlayMusicService
import com.example.muzix.ultis.Constants.Companion.UPDATE_STATUS_PLAYING_NOTIFICATION

class ControllerMusicReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.getIntExtra("action",0)
        val intentService = Intent(context,PlayMusicService::class.java)
        intentService.putExtra(UPDATE_STATUS_PLAYING_NOTIFICATION,action)
        context?.startService(intentService)
    }
}