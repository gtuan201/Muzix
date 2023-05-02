package com.example.muzix.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.muzix.R
import com.example.muzix.model.Song
import com.example.muzix.ultis.ControllerMusicReceiver
import com.example.muzix.ultis.NotificationApplication.Companion.NOTIFICATION_CHANNEL


class PlayMusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private var song: Song? = null

    companion object {
        const val ACTION_START = 1
        const val ACTION_RESUME = 2
        const val ACTION_PAUSE = 3
        const val ACTION_CLEAR = 4
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "play") {
            song = intent.getParcelableExtra("song")
            if (song != null) {
                startMusic(song)
                sendNotification(song)
            }
        }
        val actionMusic = intent?.getIntExtra("action_service", 0)
        if (actionMusic != null) handleActionMusic(actionMusic)
        if (mediaPlayer != null) {
            mediaPlayer?.setOnCompletionListener {
                stopSelf()
            }
        }
        nextSongRecommend()
        return START_NOT_STICKY
    }

    private fun startMusic(song: Song?) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(song?.mp3))
        }
        mediaPlayer?.start()
        isPlaying = true
        sendActionToActivity(ACTION_START)
    }

    private fun resumeMusic() {
        if (!isPlaying && mediaPlayer != null) {
            mediaPlayer?.start()
            isPlaying = true
            sendNotification(song)
            sendActionToActivity(ACTION_RESUME)
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            sendNotification(song)
            sendActionToActivity(ACTION_PAUSE)
        }
    }

    private fun sendNotification(song: Song?) {
        val remoteViews = RemoteViews(packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.notification_title, song?.name)
        remoteViews.setTextViewText(R.id.notification_artist, song?.artist)
        if (isPlaying) {
            remoteViews.setOnClickPendingIntent(
                R.id.notification_play_pause,
                getPendingIntent(ACTION_PAUSE)
            )
            remoteViews.setImageViewResource(R.id.notification_play_pause, R.drawable.ic_pause)
        } else {
            remoteViews.setOnClickPendingIntent(
                R.id.notification_play_pause,
                getPendingIntent(ACTION_RESUME)
            )
            remoteViews.setImageViewResource(R.id.notification_play_pause, R.drawable.ic_play)
        }
        remoteViews.setOnClickPendingIntent(
            R.id.notification_cancel,
            getPendingIntent(ACTION_CLEAR)
        )
//        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setSmallIcon(R.drawable.baseline_music_note_24)
//            .setLargeIcon(bitmap)
//            .setCustomContentView(remoteViews)
//            .build()

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentTitle(song?.name)
            .setContentText(song?.artist)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.logo))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (isPlaying) {
            notificationBuilder.addAction(R.drawable.ic_previous, "Previous", null)
                .addAction(R.drawable.ic_pause, "Pause", getPendingIntent(ACTION_PAUSE))
                .addAction(R.drawable.ic_next, "Next", null)
                .addAction(R.drawable.ic_clear,"Clear",getPendingIntent(ACTION_CLEAR))
        }
        else {
            notificationBuilder.addAction(R.drawable.ic_previous, "Previous", null)
                .addAction(R.drawable.ic_play, "Play", getPendingIntent(ACTION_RESUME))
                .addAction(R.drawable.ic_next, "Next", null)
                .addAction(R.drawable.ic_clear,"Clear",getPendingIntent(ACTION_CLEAR))
        }
        startForeground(1, notificationBuilder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getPendingIntent(action: Int): PendingIntent? {
        val intent = Intent(this, ControllerMusicReceiver::class.java)
        intent.putExtra("action", action)
        return PendingIntent.getBroadcast(this, action, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun nextSongRecommend() {
        if (isSongAlmostFinished()) {
            Log.e("isSongAlmostFinished", "true")
        }
    }

    private fun isSongAlmostFinished(): Boolean {
        val currentPosition = mediaPlayer?.currentPosition ?: 0
        val totalDuration = mediaPlayer?.duration ?: 0
        val percentage = (currentPosition.toFloat() / totalDuration.toFloat()) * 100
        return percentage >= 10
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_PAUSE -> pauseMusic()
            ACTION_RESUME -> resumeMusic()
            ACTION_CLEAR -> {
                stopSelf()
                sendActionToActivity(ACTION_CLEAR)
            }
        }
    }

    private fun sendActionToActivity(action: Int) {
        val intent = Intent("send_action")
        val bundle = Bundle()
        bundle.putParcelable("song", song)
        bundle.putBoolean("isPlaying", isPlaying)
        bundle.putInt("action", action)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}
