package com.example.muzix

import android.content.*
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.muzix.databinding.ActivityMainBinding
import com.example.muzix.model.*
import com.example.muzix.service.PlayMusicService
import com.example.muzix.service.PlayMusicService.Companion.ACTION_CLEAR
import com.example.muzix.service.PlayMusicService.Companion.ACTION_PAUSE
import com.example.muzix.service.PlayMusicService.Companion.ACTION_RESUME
import com.example.muzix.service.PlayMusicService.Companion.ACTION_START
import com.example.muzix.view.home.HomeFragment
import com.example.muzix.view.LibraryFragment
import com.example.muzix.view.PremiumFragment
import com.example.muzix.view.SearchFragment


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
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                song = bundle.getParcelable("song")
                isPlaying = bundle.getBoolean("isPlaying")
                action = bundle.getInt("action")
                showNowPlaying(action)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter("send_action"))
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
                    changeFragment(homeFragment)
                    checkBackStack()
                    true
                }
                R.id.icon_search -> {
                    changeFragment(searchFragment)
                    checkBackStack()
                    true
                }
                R.id.icon_library -> {
                    changeFragment(libraryFragment)
                    checkBackStack()
                    true
                }
                R.id.icon_premium -> {
                    changeFragment(premiumFragment)
                    checkBackStack()
                    true
                }
                else -> {
                    false
                }
            }
        }
//        val email = intent.getStringExtra("Email")
//        Log.e("email", email.toString())
//        for (i in 1..9) {
//            val timestamp = System.currentTimeMillis().toString()
//            FirebaseService.apiService.addSong(timestamp,Song(timestamp,"name","duration","des","image",
//            "artis","mp3","id",0,"Rap"))
//                .enqueue(object : Callback<Song>{
//                    override fun onResponse(call: Call<Song>, response: Response<Song>) {
//                        Log.e("ok","ok")
//                    }
//
//                    override fun onFailure(call: Call<Song>, t: Throwable) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//        }
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
                if (isPlaying) sendActionToService(ACTION_PAUSE)
                else sendActionToService(ACTION_RESUME)
            }
        }
    }

    private fun setStatusButton() {
        if (isPlaying) {
            binding.btnPlayOrPause.setImageResource(R.drawable.ic_pause)
        } else binding.btnPlayOrPause.setImageResource(R.drawable.ic_play)
    }

    private fun sendActionToService(action: Int) {
        val intent = Intent(this, PlayMusicService::class.java)
        intent.putExtra("action_service", action)
        startService(intent)
    }

    fun switchFragment(fragment: Fragment, playlist: Playlist) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putParcelable("playlist", playlist)
        fragment.arguments = bundle
        fragmentTransaction.add(R.id.fragment_container, fragment).addToBackStack("playlist_detail")
            .hide(active).show(fragment).commit()
        active = fragment
    }

    private fun checkBackStack() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else super.onBackPressed()
    }
}