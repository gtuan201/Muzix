package com.example.muzix.ultis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.muzix.service.PlayMusicService

class ControllerMusicReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.getIntExtra("action",0)
        val intentService = Intent(context,PlayMusicService::class.java)
        intentService.putExtra("action_service",action)
        context?.startService(intentService)
    }
}