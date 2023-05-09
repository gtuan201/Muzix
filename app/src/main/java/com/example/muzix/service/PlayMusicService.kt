package com.example.muzix.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.muzix.R
import com.example.muzix.model.Song
import com.example.muzix.ultis.Constants.Companion.ACTION_UPDATE_STATUS_PLAYING
import com.example.muzix.ultis.Constants.Companion.PLAY
import com.example.muzix.ultis.Constants.Companion.SEND_CURRENT_SONG
import com.example.muzix.ultis.Constants.Companion.UPDATE_PROGRESS_PLAYING
import com.example.muzix.ultis.Constants.Companion.UPDATE_STATUS_PLAYING_NOTIFICATION
import com.example.muzix.ultis.ControllerMusicReceiver
import com.example.muzix.ultis.NotificationApplication.Companion.NOTIFICATION_CHANNEL
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlin.random.Random


class PlayMusicService : Service() {
    private var isPlaying: Boolean = false
    private var song: Song? = null
    private var playlist : ArrayList<Song>? = null
    private var position : Int = 0
    private var currentPosition : Int = 0
    private lateinit var player: ExoPlayer
    private lateinit var listPlayedSong : MutableList<Int>

    //variable update progress
    private val handler = Handler(Looper.getMainLooper())
    private var progressRunnable: Runnable? = null

    //variable listener exoplayer
    private val listenerEnd = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                if (!isPlayingPlaylist()) stopSelf()
                else nextSongRecommend()
            }
        }
    }


    companion object {
        const val ACTION_START = 1
        const val ACTION_RESUME = 2
        const val ACTION_PAUSE = 3
        const val ACTION_CLEAR = 4
        const val ACTION_NEXT = 5
        const val ACTION_PREVIOUS = 6
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == PLAY) {
            song = intent.getParcelableExtra("song")
            playlist = intent.getParcelableArrayListExtra("playlist")
            position = intent.getIntExtra("position",0)
            if (song != null) {
                startMusic(song)
                sendNotification(song)
            }
            if (isPlayingPlaylist()){
                listPlayedSong = mutableListOf()
                startMusic(playlist?.get(position))
                sendNotification(playlist?.get(position))
                sendCurrentSongToActivity(playlist?.get(position))
                currentPosition = position
                listPlayedSong.add(position)
            }
        }
        val actionMusic = intent?.getIntExtra(UPDATE_STATUS_PLAYING_NOTIFICATION, 0)
        if (actionMusic != null) {
            handleActionMusic(actionMusic)
        }
        return START_NOT_STICKY
    }

    private fun startMusic(song: Song?) {
        if (isPlaying) {
            player.release()
            handler.removeCallbacks(progressRunnable!!)
        }
        val mediaItem = MediaItem.fromUri(song?.mp3.toString())
        player = ExoPlayer.Builder(this).build()
        player.addListener(listenerEnd)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        updateProgress()
        isPlaying = true
        sendActionToActivity(ACTION_START,song)
    }

    private fun resumeMusic() {
        if (!isPlaying) {
            player.play()
            isPlaying = true
            if (isPlayingPlaylist()) {
                sendNotification(playlist?.get(currentPosition))
                sendActionToActivity(ACTION_PAUSE,playlist?.get(currentPosition))
            }
            else {
                sendNotification(this.song)
                sendActionToActivity(ACTION_PAUSE, this.song)
            }
            updateProgress()
        }
    }

    private fun pauseMusic() {
        if (isPlaying) {
            player.pause()
            isPlaying = false
            if (isPlayingPlaylist()) {
                sendNotification(playlist?.get(currentPosition))
                sendActionToActivity(ACTION_PAUSE,playlist?.get(currentPosition))
            }
            else {
                sendNotification(this.song)
                sendActionToActivity(ACTION_PAUSE, this.song)
            }
            handler.removeCallbacks(progressRunnable!!)
        }
    }
    private fun nextSongRecommend() {
        if (playlist != null){
            var nextPosition = 0
            while (listPlayedSong.contains(nextPosition)){
                if (listPlayedSong.size == playlist?.size){
                    listPlayedSong.clear()
                    break
                }
                nextPosition = Random.nextInt(playlist?.size ?: 0)
            }
            Log.e("playlist_size",playlist?.size.toString())
            val nextSong = playlist?.get(nextPosition)
            listPlayedSong.add(nextPosition)
            Log.e("played",listPlayedSong.size.toString())
            currentPosition = nextPosition
            sendCurrentSongToActivity(nextSong)
            startMusic(nextSong)
            sendNotification(nextSong)
        }
    }

    private fun previousSong(){
        if (playlist != null){
            if (listPlayedSong.size > 1){
                val currentIndex = listPlayedSong.indexOf(currentPosition)
                val previousSong = playlist?.get(listPlayedSong[currentIndex - 1])
                listPlayedSong.removeAt(currentIndex)
                sendCurrentSongToActivity(previousSong)
                startMusic(previousSong)
                sendNotification(previousSong)
                currentPosition = listPlayedSong[currentIndex - 1]
            }
            else {
                val previousSong = playlist?.get(listPlayedSong[0])
                sendCurrentSongToActivity(previousSong)
                startMusic(previousSong)
                sendNotification(previousSong)
            }
        }
    }

    private fun sendNotification(song: Song?){
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentTitle(song?.name)
            .setContentText(song?.artist)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logo))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (isPlaying) {
            notificationBuilder.addAction(R.drawable.ic_previous, "Previous", getPendingIntent(ACTION_PREVIOUS))
                .addAction(R.drawable.ic_pause, "Pause", getPendingIntent(ACTION_PAUSE))
                .addAction(R.drawable.ic_next, "Next", getPendingIntent(ACTION_NEXT))
                .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(ACTION_CLEAR))
        } else {
            notificationBuilder.addAction(R.drawable.ic_previous, "Previous", getPendingIntent(ACTION_PREVIOUS))
                .addAction(R.drawable.ic_play, "Play", getPendingIntent(ACTION_RESUME))
                .addAction(R.drawable.ic_next, "Next", getPendingIntent(ACTION_NEXT))
                .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(ACTION_CLEAR))
        }
        startForeground(1, notificationBuilder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getPendingIntent(action: Int): PendingIntent? {
        val intent = Intent(this, ControllerMusicReceiver::class.java)
        intent.putExtra("action", action)
        return PendingIntent.getBroadcast(this, action, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_PAUSE -> pauseMusic()
            ACTION_RESUME -> resumeMusic()
            ACTION_NEXT -> nextSongRecommend()
            ACTION_PREVIOUS -> previousSong()
            ACTION_CLEAR -> {
                sendCurrentSongToActivity(null)
                sendActionToActivity(ACTION_CLEAR,null)
                stopSelf()
            }
        }
    }

    private fun sendActionToActivity(action: Int, song: Song?) {
        val intent = Intent(ACTION_UPDATE_STATUS_PLAYING)
        val bundle = Bundle()
        bundle.putParcelable("song", song)
        bundle.putBoolean("isPlaying", isPlaying)
        bundle.putInt("action", action)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun updateProgress() {
        progressRunnable = Runnable {
            val progress = player.currentPosition
            val max = player.duration
            val intent = Intent(UPDATE_PROGRESS_PLAYING)
            val bundle = Bundle()
            bundle.putLong("progress", progress)
            bundle.putLong("max", max)
            intent.putExtras(bundle)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            handler.postDelayed(progressRunnable!!, 1000)
        }
        handler.post(progressRunnable!!)
    }

    private fun sendCurrentSongToActivity(song: Song?){
        val intent = Intent(SEND_CURRENT_SONG)
        intent.putExtra("song",song)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun isPlayingPlaylist() : Boolean{
        return playlist != null
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressRunnable!!)
        player.release()
    }
}
