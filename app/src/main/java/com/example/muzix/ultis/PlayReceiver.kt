package com.example.muzix.ultis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.muzix.model.Song
import com.example.muzix.service.PlayMusicService
import com.example.muzix.ultis.Constants.Companion.PLAY

class PlayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val s = intent?.getParcelableExtra<Song>("key")
        val playlist = intent?.getParcelableArrayListExtra<Song>("playlist")
        val position = intent?.getIntExtra("position",0)
        val intentService = Intent(context,PlayMusicService::class.java)
        intentService.action = PLAY
        intentService.putExtra("song",s)
        intentService.putExtra("position",position)
        intentService.putParcelableArrayListExtra("playlist",playlist)
        context?.startService(intentService)
    }
}