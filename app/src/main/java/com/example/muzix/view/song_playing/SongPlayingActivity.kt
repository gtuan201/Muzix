package com.example.muzix.view.song_playing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.muzix.R
import com.example.muzix.databinding.ActivitySongPlayingBinding
import com.example.muzix.model.Song
import com.example.muzix.service.PlayMusicService
import com.example.muzix.service.PlayMusicService.Companion.ACTION_NEXT
import com.example.muzix.service.PlayMusicService.Companion.ACTION_PAUSE
import com.example.muzix.service.PlayMusicService.Companion.ACTION_PREVIOUS
import com.example.muzix.service.PlayMusicService.Companion.ACTION_RESUME
import com.example.muzix.ultis.Constants
import com.example.muzix.ultis.Constants.Companion.ACTION_UPDATE_STATUS_PLAYING
import com.example.muzix.ultis.Constants.Companion.UPDATE_PROGRESS_PLAYING
import com.example.muzix.viewmodel.PlaylistViewModel

class SongPlayingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongPlayingBinding
    private var progress: Long = 0
    private var isPlaying : Boolean = false
    private var song : Song? = null
    private var max: Long = 0
    private var viewModel : PlaylistViewModel? = null
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                song = bundle.getParcelable("song")
                showInforSong(song,viewModel)
                isPlaying = bundle.getBoolean("isPlaying")
                showPlayOrPause(isPlaying)
            }
        }
    }

    private val receiverProgress : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            if (bundle != null) {
                progress = bundle.getLong("progress")
                max = bundle.getLong("max")
                updateProgress()
            }
        }
    }

    private fun updateProgress() {
        binding.progressBar.max = max.toInt()
        binding.progressBar.progress = progress.toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PlaylistViewModel::class.java]
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverProgress, IntentFilter(UPDATE_PROGRESS_PLAYING))
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            IntentFilter(ACTION_UPDATE_STATUS_PLAYING)
        )
        song = intent.getParcelableExtra("song")
        isPlaying = intent.getBooleanExtra("isPlaying",true)
        max = intent.getLongExtra("max",0)
        progress = intent.getLongExtra("progress",0)
        updateProgress()
        showInforSong(song,viewModel)
        showPlayOrPause(isPlaying)
        binding.btnPlay.setOnClickListener {
            playOrPause(isPlaying)
        }
        binding.btnNext.setOnClickListener { sendActionToService(ACTION_NEXT) }
        binding.btnPrevious.setOnClickListener { sendActionToService(ACTION_PREVIOUS) }
    }

    private fun showInforSong(song: Song?, viewModel: PlaylistViewModel?) {
        viewModel?.getPlaylist(song?.idPlaylist.toString())?.observe(this){
            binding.tvNamePlaylist.text = it.name
        }
        Glide.with(this).load(song?.image).placeholder(R.drawable.thumbnail).into(binding.imgSong)
        colorBackground(binding.layoutSong,song)
        binding.tvNameSong.text = song?.name
        binding.tvArtist.text = song?.artist
    }


    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverProgress)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
    private fun sendActionToService(action: Int) {
        val intent = Intent(this, PlayMusicService::class.java)
        intent.putExtra(Constants.UPDATE_STATUS_PLAYING_NOTIFICATION, action)
        startService(intent)
    }
    private fun showPlayOrPause(playing: Boolean) {
        if (playing){
            binding.btnPlay.setImageResource(R.drawable.baseline_pause_24)
        }
        else{
            binding.btnPlay.setImageResource(R.drawable.baseline_play_arrow_24)
        }
    }

    private fun playOrPause(playing: Boolean) {
        if (playing) {
            sendActionToService(ACTION_PAUSE)
            isPlaying = false
            binding.btnPlay.setImageResource(R.drawable.baseline_play_arrow_24)
        }
        else {
            sendActionToService(ACTION_RESUME)
            isPlaying = true
            binding.btnPlay.setImageResource(R.drawable.baseline_pause_24)
        }
    }

    private fun colorBackground(layoutSong: RelativeLayout, song: Song?) {
        Glide.with(this).asBitmap().load(song?.image)
            .into(object :  CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate { palette ->
                        val darkMutedSwatch = palette?.darkMutedSwatch
                        val darkColor = darkMutedSwatch?.rgb ?: Color.TRANSPARENT
                        val colors = intArrayOf(darkColor, ContextCompat.getColor(this@SongPlayingActivity,R.color.main_background))
                        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                        layoutSong.background = gradientDrawable
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.e("onLoadCleared","error")
                }

            })
    }
}