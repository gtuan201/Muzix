package com.example.muzix.view.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.muzix.view.main.MainActivity
import com.example.muzix.databinding.FragmentHomeBinding
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.ultis.PlayReceiver
import com.example.muzix.view.playlist_detail.PlaylistDetailFragment
import com.example.muzix.viewmodel.PlaylistViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(),HomeChildAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeParentAdapter
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private var song : Song? = null
    private var listPlaylistHistory : MutableList<Playlist> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        setUpRcv()
        val viewModel = ViewModelProvider(requireActivity())[PlaylistViewModel::class.java]
        viewModel.getCollection().observe(requireActivity()) {
            adapter.setDataCollection(it)
            binding.rcvHome.adapter = adapter
            adapter.notifyDataSetChanged()
            updateUI(viewModel)
        }
        viewModel.getHistory().observe(requireActivity()){
            FirebaseService.apiService.getPlaylist().enqueue(object : Callback<Map<String,Playlist>>{
                override fun onResponse(
                    call: Call<Map<String, Playlist>>,
                    response: Response<Map<String, Playlist>>
                ) {
                    for (i in it){
                        for (j in response.body()?.values!!.toList()){
                            if(i.idPlaylist == j.id){
                                listPlaylistHistory.add(j)
                            }
                        }
                    }
                    historyAdapter.setData(listPlaylistHistory)
                    binding.rcvHistory.adapter = historyAdapter
                    historyAdapter.notifyDataSetChanged()
                    if (it.isNotEmpty()){
                        displayHistory()
                    }
                }

                override fun onFailure(call: Call<Map<String, Playlist>>, t: Throwable) {
                    Log.e("error",t.message.toString())
                }

            })
        }
        viewModel.getTopArtist().observe(requireActivity()){
            artistAdapter = ArtistAdapter(it)
            binding.rcvArtist.adapter = artistAdapter
            artistAdapter.notifyDataSetChanged()
        }
        binding.btnPlay.setOnClickListener {
            val intent = Intent(context,PlayReceiver::class.java)
            intent.putExtra("key",song)
            context?.sendBroadcast(intent)
        }
        return binding.root
    }

    private fun displayHistory() {
       Handler().postDelayed({
           binding.titleHistory.visibility = View.VISIBLE
           binding.rcvHistory.visibility = View.VISIBLE
       },500)
    }

    private fun setUpRcv() {
        binding.rcvHome.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcvHome.setHasFixedSize(true)
        binding.rcvArtist.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.rcvArtist.setHasFixedSize(true)
        adapter = HomeParentAdapter(this@HomeFragment)
        binding.rcvHistory.layoutManager = GridLayoutManager(context,2)
        binding.rcvHistory.setHasFixedSize(true)
        historyAdapter = HistoryAdapter()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(viewModel: PlaylistViewModel) {
        Handler().postDelayed({
            binding.progressLoading.visibility = View.GONE
            viewModel.getRandomSong().observe(requireActivity()){
                Glide.with(binding.imgRandomSong).load(it.image).into(binding.imgRandomSong)
                binding.tvNameSong.text = "${it.name} - ${it.artist}"
                song = it
            }
            binding.rcvHome.visibility = View.VISIBLE
            binding.tvTitleSong.visibility = View.VISIBLE
            binding.rcvArtist.visibility = View.VISIBLE
            binding.tvTitleArtist.visibility = View.VISIBLE
            binding.imgRandomSong.visibility = View.VISIBLE
            binding.btnPlay.visibility = View.VISIBLE
        },1000)
    }

    override fun onItemClick(playlist: Playlist) {
        val playlistDetailFragment = PlaylistDetailFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(playlistDetailFragment,playlist)
        }
    }
}