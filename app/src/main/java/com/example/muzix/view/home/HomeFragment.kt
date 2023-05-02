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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.muzix.databinding.FragmentHomeBinding
import com.example.muzix.model.Song
import com.example.muzix.ultis.PlayReceiver
import com.example.muzix.viewmodel.PlaylistViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeParentAdapter
    private lateinit var artistAdapter: ArtistAdapter
    private var song : Song? = null
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

    private fun setUpRcv() {
        binding.rcvHome.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcvHome.setHasFixedSize(true)
        binding.rcvArtist.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.rcvArtist.setHasFixedSize(true)
        adapter = HomeParentAdapter()
    }

    private fun updateUI(viewModel: PlaylistViewModel) {
        Handler().postDelayed({
            binding.progressLoading.visibility = View.GONE
            viewModel.getRandomSong().observe(requireActivity()){
                Glide.with(binding.imgRandomSong).load(it.image).into(binding.imgRandomSong)
                binding.tvNameSong.text = it.name
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
}