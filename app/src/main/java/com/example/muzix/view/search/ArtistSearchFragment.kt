package com.example.muzix.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.muzix.R
import com.example.muzix.databinding.FragmentArtistSearchBinding
import com.example.muzix.model.Artist
import com.example.muzix.ultis.OnArtistClick
import com.example.muzix.view.artist_detail.ArtistDetailFragment
import com.example.muzix.view.main.MainActivity
import com.example.muzix.viewmodel.SearchViewModel

class ArtistSearchFragment : Fragment(),OnArtistClick {

    private lateinit var binding : FragmentArtistSearchBinding
    private lateinit var adapter: ArtistSearchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentArtistSearchBinding.inflate(LayoutInflater.from(context),container,false)
        binding.rcvArtistSearch.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        adapter = ArtistSearchAdapter(this@ArtistSearchFragment)
        binding.rcvArtistSearch.adapter = adapter
        val viewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        viewModel.getDataSearch().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                viewModel.getListArtist(it).observe(viewLifecycleOwner){list->
                    adapter.setData(list)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        return binding.root
    }

    override fun onArtistClick(artist: Artist) {
        val artistDetailFragment = ArtistDetailFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(artistDetailFragment,artist)
        }
    }
}