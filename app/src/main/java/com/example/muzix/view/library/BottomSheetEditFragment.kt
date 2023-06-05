package com.example.muzix.view.library

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.FragmentBottomSheetEditBinding
import com.example.muzix.model.Playlist
import com.example.muzix.viewmodel.LibraryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetEditFragment(
    private val playList: Playlist,
    private val viewModel: LibraryViewModel,
    private val listLib: MutableList<Any>,
    private val dialogParent: BottomSheetDialog
) : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentBottomSheetEditBinding
    private val playlistData : Playlist = playList
    private var playlistUpdate : Playlist? = null
    private var name : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        binding = FragmentBottomSheetEditBinding.inflate(LayoutInflater.from(context))
        Glide.with(binding.imgPlaylist).load(playList.thumbnail).placeholder(R.drawable.thumbnail).into(binding.imgPlaylist)
        binding.edtNamePlaylist.setText(playList.name)
        playlistUpdate = playlistData
        binding.imgPlaylist.setOnClickListener {
            pickImage()
        }
        binding.save.setOnClickListener {
            name = binding.edtNamePlaylist.text.toString().trim()
            playlistUpdate?.name = name
            listLib[listLib.indexOf(playList)] = playlistUpdate!!
            viewModel.updateLib(listLib)
            viewModel.updatePlaylistLib(playlistUpdate!!)
            dialog.dismiss()
            dialogParent.dismiss()
        }
        dialog.setContentView(binding.root)
        return dialog
    }
    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        uri?.let {
            Glide.with(binding.imgPlaylist).load(it).into(binding.imgPlaylist)
            playlistUpdate = Playlist(
                playlistData.id,
                playlistData.name,
                playlistData.description,
                playlistData.owner,
                playlistData.dayCreate,it.toString(),
                playlistData.tracks, playlistData.duration, playlistData.lover, playlistData.idCollection
            )
        }
    }
}