package com.example.muzix.view.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.muzix.R
import com.example.muzix.databinding.FragmentMyPlaylistBinding


class MyPlaylistFragment : Fragment() {


    private lateinit var binding : FragmentMyPlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyPlaylistBinding.inflate(LayoutInflater.from(context),container,false)
        return binding.root
    }
}