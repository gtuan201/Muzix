package com.example.muzix.ultis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.muzix.model.Song
import com.example.muzix.service.PlayMusicService

class PlayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val s = intent?.getParcelableExtra<Song>("key")
        Log.e("s", s.toString())
        val intentService = Intent(context,PlayMusicService::class.java)
        intentService.action = "play"
        intentService.putExtra("song",s)
        context?.startService(intentService)
    }
}