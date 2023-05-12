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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.muzix.databinding.FragmentSongSearchBinding
import com.example.muzix.viewmodel.SearchViewModel

class SongSearchFragment : Fragment() {

    private lateinit var binding: FragmentSongSearchBinding
    private lateinit var adapter: SongAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSongSearchBinding.inflate(LayoutInflater.from(context),container,false)
        adapter = SongAdapter()
        binding.rcvSongSearch.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rcvSongSearch.adapter = adapter
        val viewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        viewModel.getDataSearch().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                viewModel.getListSong(it).observe(viewLifecycleOwner){list->
                    adapter.setData(list)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        return binding.root
    }
}