package com.example.muzix.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.view.main.MainActivity
import com.example.muzix.databinding.FragmentHomeBinding
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.example.muzix.model.Artist
import com.example.muzix.listener.OnArtistClick
import com.example.muzix.listener.OnItemClickListener
import com.example.muzix.ultis.PlayReceiver
import com.example.muzix.view.artist_detail.ArtistDetailFragment
import com.example.muzix.view.notification.NotificationFragment
import com.example.muzix.view.playlist_detail.PlaylistDetailFragment
import com.example.muzix.view.setting.SettingFragment
import com.example.muzix.viewmodel.PlaylistViewModel
import java.util.Calendar
import java.util.Collections

class HomeFragment : Fragment(), OnItemClickListener, OnArtistClick {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeParentAdapter
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private var song : Song? = null
    private var randomPosition : Int = 0
    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(mContext), container, false)
        setUpRcv()
        binding.tvWelcome.text = welcomeTitle()
        val preferences = context?.getSharedPreferences("MyPreferences",Context.MODE_PRIVATE)
        randomPosition = preferences?.getInt("id",0) ?: 0
        val viewModel = ViewModelProvider(requireActivity())[PlaylistViewModel::class.java]
        viewModel.getRandomSong(randomPosition).observe(requireActivity()){
            Glide.with(binding.imgRandomSong).load(it.image).into(binding.imgRandomSong)
            binding.tvNameSong.text = "${it.name} - ${it.artist}"
            song = it
        }
        // listened playlists
        viewModel.getPlaylistHistory().observe(viewLifecycleOwner){
            historyAdapter.setData(it.take(4))
            historyAdapter.notifyDataSetChanged()
        }
        viewModel.getCollection().observe(requireActivity()) {
            adapter.setDataCollection(it)
            binding.rcvHome.adapter = adapter
            adapter.notifyDataSetChanged()
            updateUI()
        }
        //top artist
        viewModel.getTopArtist().observe(requireActivity()){
            artistAdapter = ArtistAdapter(it,this@HomeFragment)
            binding.rcvArtist.adapter = artistAdapter
            artistAdapter.notifyDataSetChanged()
        }
        binding.btnPlay.setOnClickListener {
            val intent = Intent(context,PlayReceiver::class.java)
            intent.putExtra("key",song)
            context?.sendBroadcast(intent)
        }
        binding.btnSetting.setOnClickListener {
            changeFragment(SettingFragment())
        }
        binding.btnNotification.setOnClickListener {
            changeFragment(NotificationFragment())
        }
        return binding.root
    }


    private fun setUpRcv() {
        binding.rcvHome.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcvHome.setHasFixedSize(true)
        binding.rcvArtist.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.rcvArtist.setHasFixedSize(true)
        adapter = HomeParentAdapter(this@HomeFragment)
        binding.rcvHistory.layoutManager = GridLayoutManager(context,2)
        binding.rcvHistory.setHasFixedSize(true)
        historyAdapter = HistoryAdapter(this@HomeFragment)
        binding.rcvHistory.adapter = historyAdapter
    }

    private fun updateUI() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressLoading.visibility = View.GONE
            binding.rcvHome.visibility = View.VISIBLE
            binding.imgRandomSong.visibility = View.VISIBLE
            binding.tvTitleSong.visibility = View.VISIBLE
            binding.tvNameSong.visibility = View.VISIBLE
            binding.rcvArtist.visibility = View.VISIBLE
            binding.tvTitleArtist.visibility = View.VISIBLE
            binding.imgRandomSong.visibility = View.VISIBLE
            binding.btnPlay.visibility = View.VISIBLE
            binding.titleHistory.visibility = View.VISIBLE
            binding.rcvHistory.visibility = View.VISIBLE
        },1000)
    }
    private fun welcomeTitle() : String{
        val title = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 4..10 -> {
                getString(R.string.morning)
            }
            in 11..13 -> {
                getString(R.string.noon)
            }
            in 13..17 -> {
                getString(R.string.afternoon)
            }
            else -> getString(R.string.evening)
        }
        return title
    }

    override fun onItemClick(playlist: Playlist) {
        val playlistDetailFragment = PlaylistDetailFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(playlistDetailFragment,playlist)
        }
    }

    override fun onArtistClick(artist: Artist) {
        val artistDetailFragment = ArtistDetailFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(artistDetailFragment,artist)
        }
    }
    private fun changeFragment(fragment: Fragment) {
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(fragment)
        }
    }
}