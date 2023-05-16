package com.example.muzix.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.muzix.R
import com.example.muzix.databinding.FragmentPlaylistSearchBinding
import com.example.muzix.model.Playlist
import com.example.muzix.ultis.OnItemClickListener
import com.example.muzix.ultis.hiddenSoftKeyboard
import com.example.muzix.view.home.HomeChildAdapter
import com.example.muzix.view.main.MainActivity
import com.example.muzix.view.playlist_detail.PlaylistDetailFragment
import com.example.muzix.viewmodel.SearchViewModel
import com.example.muzix.viewmodel.SongViewModel

class PlaylistSearchFragment() : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentPlaylistSearchBinding
    private lateinit var adapter: PlaylistAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistSearchBinding.inflate(LayoutInflater.from(context),container,false)
        binding.rcvPlaylistSearch.layoutManager = GridLayoutManager(requireContext(),2)
        adapter = PlaylistAdapter(this)
        binding.rcvPlaylistSearch.adapter = adapter
        val viewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        viewModel.getDataSearch().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                viewModel.getListPlaylist(it).observe(viewLifecycleOwner){list->
                    adapter.setData(list)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        return binding.root
    }

    override fun onItemClick(playlist: Playlist) {
        val playlistDetailFragment = PlaylistDetailFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(playlistDetailFragment,playlist)
            hiddenSoftKeyboard(activity)
        }
    }
}