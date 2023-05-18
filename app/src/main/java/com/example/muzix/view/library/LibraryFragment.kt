package com.example.muzix.view.library

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.BottomSheetAddPlaylistBinding
import com.example.muzix.databinding.FragmentLibraryBinding
import com.example.muzix.model.Playlist
import com.example.muzix.ultis.CustomComparator
import com.example.muzix.view.main.MainActivity
import com.example.muzix.viewmodel.LibraryViewModel
import com.example.muzix.viewmodel.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class LibraryFragment : Fragment() {

    private lateinit var binding : FragmentLibraryBinding
    private lateinit var adapter: LibraryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(LayoutInflater.from(context),container,false)
        binding.rcvLib.layoutManager = GridLayoutManager(context,2)
        val viewModel = ViewModelProvider(this)[PlaylistViewModel::class.java]
        val viewModelLib = ViewModelProvider(this)[LibraryViewModel::class.java]
        viewModelLib.getLibrary().observe(viewLifecycleOwner){
            Collections.sort(it,CustomComparator())
            adapter = LibraryAdapter(it)
            binding.rcvLib.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        val imgProfile = FirebaseAuth.getInstance().currentUser?.photoUrl
        Glide.with(binding.imgProfile).load(imgProfile).placeholder(R.drawable.thumbnail).into(binding.imgProfile)
        binding.btnAdd.setOnClickListener {
            openDialogAdd(viewModel)
        }
        return binding.root
    }

    private fun openDialogAdd(viewModel: PlaylistViewModel) {
        val dialog = BottomSheetDialog(requireContext())
        val bindingBottom :BottomSheetAddPlaylistBinding = BottomSheetAddPlaylistBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bindingBottom.root)
        bindingBottom.btnCancel.setOnClickListener { dialog.dismiss() }
        bindingBottom.btnAddPlaylist.setOnClickListener {
            val namePlaylist = bindingBottom.edtName.text.toString().trim()
            dialog.dismiss()
            addPlaylistToLib(namePlaylist,viewModel)
        }
        dialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun addPlaylistToLib(namePlaylist: String, viewModel: PlaylistViewModel) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid.toString()
        val timeStamp = System.currentTimeMillis().toString()
        val calendar = Calendar.getInstance().time
        val dateCreate = SimpleDateFormat("dd-MM-yyyy").format(calendar)
        val playlist = Playlist(timeStamp,namePlaylist,"",user?.displayName,dateCreate,user?.photoUrl.toString(), null,"0",0,uid)
        viewModel.addPlaylist(playlist)
        switchFragment(playlist)
    }

    private fun switchFragment(
        playlist: Playlist
    ) {
        val addSongPlaylistFragment = AddSongPlaylistFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(addSongPlaylistFragment,playlist)
        }
    }
}