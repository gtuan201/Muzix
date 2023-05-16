package com.example.muzix.view.main

import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daimajia.swipe.SimpleSwipeListener
import com.daimajia.swipe.SwipeLayout
import com.example.muzix.R
import com.example.muzix.databinding.ActivityMainBinding
import com.example.muzix.model.*
import com.example.muzix.service.PlayMusicService.Companion.ACTION_CLEAR
import com.example.muzix.service.PlayMusicService.Companion.ACTION_NEXT
import com.example.muzix.service.PlayMusicService.Companion.ACTION_PAUSE
import com.example.muzix.service.PlayMusicService.Companion.ACTION_RESUME
import com.example.muzix.service.PlayMusicService.Companion.ACTION_START
import com.example.muzix.ultis.Constants.Companion.ACTION_UPDATE_STATUS_PLAYING
import com.example.muzix.ultis.Constants.Companion.SEND_CURRENT_SONG
import com.example.muzix.ultis.Constants.Companion.UPDATE_PROGRESS_PLAYING
import com.example.muzix.ultis.hiddenSoftKeyboard
import com.example.muzix.ultis.sendActionToService
import com.example.muzix.view.library.LibraryFragment
import com.example.muzix.view.PremiumFragment
import com.example.muzix.view.home.HomeFragment
import com.example.muzix.view.search.SearchFragment
import com.example.muzix.view.song_playing.SongPlayingActivity
import com.example.muzix.viewmodel.PlaylistViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var homeFragment: HomeFragment = HomeFragment()
    private var searchFragment: SearchFragment = SearchFragment()
    private var libraryFragment: LibraryFragment = LibraryFragment()
    private var premiumFragment: PremiumFragment = PremiumFragment()
    private lateinit var fragmentTransaction: FragmentTransaction
    private lateinit var active: Fragment
    private var song: Song? = null
    private var isPlaying: Boolean = false
    private var action: Int? = null
    private var progress: Long = 0
    private var max: Long = 0

    //    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                song = bundle.getParcelable("song")
                isPlaying = bundle.getBoolean("isPlaying")
                action = bundle.getInt("action")
                showNowPlaying(action)
                backgroundLayoutNowPlaying(song)
                sendIsPlayingToFragment(isPlaying)
            }
        }
    }


    private val receiverProgress: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                progress = bundle.getLong("progress")
                max = bundle.getLong("max")
                updateProgress()
            }
        }
    }

    private val currentSongReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val song = intent?.getParcelableExtra<Song>("song")
            if (song != null) {
                sendToFragment(song)
            } else {
                sendToFragment(null)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerBroadcast()
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.apply {
            add(R.id.fragment_container, homeFragment)
            add(R.id.fragment_container, searchFragment).hide(searchFragment)
            add(R.id.fragment_container, libraryFragment).hide(libraryFragment)
            add(R.id.fragment_container, premiumFragment).hide(premiumFragment)
        }.commit()
        active = homeFragment
        binding.nav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.icon_home -> {
                    hiddenSoftKeyboard(this)
                    fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.hide(active).show(homeFragment).hide(searchFragment)
                        .commit()
                    active = homeFragment
                    true
                }
                R.id.icon_search -> {
                    checkBackStack()
                    changeFragment(searchFragment)
                    true
                }
                R.id.icon_library -> {
                    checkBackStack()
                    changeFragment(libraryFragment)
                    true
                }
                R.id.icon_premium -> {
                    checkBackStack()
                    changeFragment(premiumFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }
        binding.layoutInfor.setOnClickListener { openSongPlayingDetail() }
        binding.layoutNowPlaying.setOnClickListener { openSongPlayingDetail() }
        binding.swipeToNext.addSwipeListener(object : SimpleSwipeListener() {
            override fun onOpen(layout: SwipeLayout?) {
                sendActionToService(ACTION_NEXT, this@MainActivity, this@MainActivity)
                layout?.close()
            }
        })
    }

    private fun openSongPlayingDetail() {
        val intent = Intent(this, SongPlayingActivity::class.java)
        intent.putExtra("song", song)
        intent.putExtra("progress", progress)
        intent.putExtra("max", max)
        intent.putExtra("isPlaying", isPlaying)
        startActivity(intent)
    }

    private fun registerBroadcast() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(ACTION_UPDATE_STATUS_PLAYING))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverProgress, IntentFilter(UPDATE_PROGRESS_PLAYING))
        LocalBroadcastManager.getInstance(this).registerReceiver(
            currentSongReceiver,
            IntentFilter(SEND_CURRENT_SONG)
        )
    }

    private fun showNowPlaying(action: Int?) {
        when (action) {
            ACTION_START -> {
                binding.layoutNowPlaying.visibility = View.VISIBLE
                showInforSong()
                setStatusButton()
            }
            ACTION_PAUSE -> {
                setStatusButton()
            }
            ACTION_RESUME -> {
                setStatusButton()
            }
            ACTION_CLEAR -> {
                binding.layoutNowPlaying.visibility = View.GONE
            }
        }
    }

    private fun changeFragment(fragment: Fragment) {
        hiddenSoftKeyboard(this)
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(active).show(fragment).commit()
        active = fragment
    }

    private fun showInforSong() {
        if (song != null) {
            Glide.with(binding.imgNowPlaying).load(song?.image).into(binding.imgNowPlaying)
            binding.tvNameNowPlaying.text = song?.name
            binding.tvArtistNowPlaying.text = song?.artist
            binding.btnPlayOrPause.setOnClickListener {
                if (isPlaying) sendActionToService(
                    ACTION_PAUSE,
                    this@MainActivity,
                    this@MainActivity
                )
                else sendActionToService(ACTION_RESUME, this@MainActivity, this@MainActivity)
            }
        }
    }

    private fun setStatusButton() {
        if (isPlaying) {
            binding.btnPlayOrPause.setImageResource(R.drawable.ic_pause)
        } else binding.btnPlayOrPause.setImageResource(R.drawable.ic_play)
    }

    private fun updateProgress() {
        binding.progressBar.max = max.toInt()
        binding.progressBar.progress = progress.toInt()
    }

    fun switchFragment(fragment: Fragment, playlist: Playlist) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putParcelable("playlist", playlist)
        fragment.arguments = bundle
        fragmentTransaction.add(R.id.fragment_container, fragment).addToBackStack(null)
            .hide(active).show(fragment).commit()
        active = fragment
    }

    fun switchFragment(fragment: Fragment, artist: Artist) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putParcelable("artist", artist)
        fragment.arguments = bundle
        fragmentTransaction.add(R.id.fragment_container, fragment).addToBackStack(null).hide(active)
            .show(fragment)
            .commit()
        active = fragment
    }

    private fun checkBackStack() {
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    private fun sendToFragment(song: Song?) {
        val viewModel = ViewModelProvider(this)[PlaylistViewModel::class.java]
        viewModel.setCurrentSong(song)
    }

    private fun sendIsPlayingToFragment(playing: Boolean) {
        val viewModel = ViewModelProvider(this)[PlaylistViewModel::class.java]
        viewModel.setIsPlaying(playing)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBroadcastReceiver()
    }

    private fun unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverProgress)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(currentSongReceiver)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 4) {
            supportFragmentManager.popBackStack()
        } else super.onBackPressed()
    }

    private fun backgroundLayoutNowPlaying(song: Song?) {
        Glide.with(this).asBitmap().load(song?.image).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                Palette.from(resource).generate { palette ->
                    val darkMutedSwatch = palette?.darkMutedSwatch
                    val darkColor = darkMutedSwatch?.rgb ?: Color.TRANSPARENT
                    binding.layoutNowPlaying.setBackgroundColor(darkColor)
                    binding.layoutSwipe.setBackgroundColor(darkColor)
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                Log.d("onLoadCleared", "error")
            }
        })
    }
//    private fun clearBackStack() {
//        val fragmentManager = supportFragmentManager
//        for (i in 0 until fragmentManager.backStackEntryCount) {
//            fragmentManager.popBackStack()
//        }
//    }
}