package com.example.muzix.view.library

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.BottomSheetAddPlaylistBinding
import com.example.muzix.databinding.FragmentLibraryBinding
import com.example.muzix.model.Playlist
import com.example.muzix.view.main.MainActivity
import com.example.muzix.viewmodel.SongViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar

class LibraryFragment : Fragment() {

    private lateinit var binding : FragmentLibraryBinding
    private var listPlaylist : MutableList<Playlist> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(LayoutInflater.from(context),container,false)
        val imgProfile = FirebaseAuth.getInstance().currentUser?.photoUrl
        Glide.with(binding.imgProfile).load(imgProfile).placeholder(R.drawable.thumbnail).into(binding.imgProfile)
        binding.btnAdd.setOnClickListener {
            openDialogAdd()
        }
        return binding.root
    }

    private fun openDialogAdd() {
        val dialog = BottomSheetDialog(requireContext())
        val bindingBottom :BottomSheetAddPlaylistBinding = BottomSheetAddPlaylistBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bindingBottom.root)
        bindingBottom.btnCancel.setOnClickListener { dialog.dismiss() }
        bindingBottom.btnAddPlaylist.setOnClickListener {
            val namePlaylist = bindingBottom.edtName.text.toString().trim()
            dialog.dismiss()
            addPlaylistToLib(namePlaylist)
        }
        dialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun addPlaylistToLib(namePlaylist: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid.toString()
        val timeStamp = System.currentTimeMillis().toString()
        val calendar = Calendar.getInstance().time
        val dateCreate = SimpleDateFormat("dd-MM-yyyy").format(calendar)
        val playlist = Playlist(timeStamp,namePlaylist,"",user?.displayName,dateCreate,user?.photoUrl.toString(),
            null,"0",0,uid)
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