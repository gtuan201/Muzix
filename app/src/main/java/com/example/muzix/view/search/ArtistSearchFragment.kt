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
import com.example.muzix.viewmodel.SearchViewModel

class ArtistSearchFragment : Fragment() {

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
        adapter = ArtistSearchAdapter()
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
}