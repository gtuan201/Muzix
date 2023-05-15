package com.example.muzix.view.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.muzix.R
import com.example.muzix.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private lateinit var binding : FragmentLibraryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(LayoutInflater.from(context),container,false)

        return binding.root
    }
}