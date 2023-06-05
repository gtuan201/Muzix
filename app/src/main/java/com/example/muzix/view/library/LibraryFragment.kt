package com.example.muzix.view.library

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.AlertSubmitDeleteBinding
import com.example.muzix.databinding.BottomSheetAddPlaylistBinding
import com.example.muzix.databinding.BottomSheetUpdatePlaylistBinding
import com.example.muzix.databinding.FragmentLibraryBinding
import com.example.muzix.listener.ClickPlaylist
import com.example.muzix.listener.LongClickToChangeImg
import com.example.muzix.model.Artist
import com.example.muzix.model.Playlist
import com.example.muzix.listener.OnArtistClick
import com.example.muzix.view.artist_detail.ArtistDetailFragment
import com.example.muzix.view.main.MainActivity
import com.example.muzix.view.playlist_detail.PlaylistDetailFragment
import com.example.muzix.viewmodel.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("NotifyDataSetChanged")
class LibraryFragment : Fragment(), ClickPlaylist, OnArtistClick, LongClickToChangeImg {

    private lateinit var binding: FragmentLibraryBinding
    private lateinit var adapter: LibraryAdapter
    private lateinit var viewModelLib : LibraryViewModel
    private var listLib : MutableList<Any> = mutableListOf()
    private var gridViewEnable = true

    companion object{
        private const val MY_PLAYLIST = 0
        private const val PLAYLIST_FAV = 1
        private const val SONG_FAV = 2
        private const val ARTIST_FAV = 3

        const val LINEAR_VIEW = 10
        const val GRID_VIEW = 11
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(LayoutInflater.from(context), container, false)
        binding.rcvLib.layoutManager = GridLayoutManager(context, 2)
        adapter = LibraryAdapter(this, this,this)
        binding.rcvLib.adapter = adapter
        viewModelLib = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        viewModelLib.getLibrary().observe(viewLifecycleOwner) {
            listLib = it
//            Collections.sort(it, CustomComparator())
            adapter.setData(it)
            adapter.notifyDataSetChanged()
            changeUI(it.isEmpty())
        }

        //pop up menu
        val popupMenu = PopupMenu(requireContext(),binding.tvFilter,Gravity.BOTTOM,0,R.style.PopupMenuStyle)
        popupMenu.inflate(R.menu.pop_up_library)
        popupMenu.menu.forEach {
            val menuItem = it
            val spannableString = SpannableString(menuItem.title)
            spannableString.setSpan(ForegroundColorSpan(Color.WHITE), 0, spannableString.length, 0)
            menuItem.title = spannableString
        }
        popupMenu.setOnMenuItemClickListener{ item->
            when(item.itemId){
                R.id.my_playlist ->{
                    binding.tvFilter.text = "Playlist của bạn"
                    updateRecyclerview(MY_PLAYLIST)
                    true
                }
                R.id.playlist_fav ->{
                    binding.tvFilter.text = "Playlist yêu thích"
                    updateRecyclerview(PLAYLIST_FAV)
                    true
                }
                R.id.song_fav ->{
                    binding.tvFilter.text = "Bài hát yêu thích"
                    updateRecyclerview(SONG_FAV)
                    true
                }
                R.id.artist_fav ->{
                    binding.tvFilter.text = "Nghệ sĩ yêu thích"
                    updateRecyclerview(ARTIST_FAV)
                    true
                }
                else -> false
            }
        }

        val imgProfile = FirebaseAuth.getInstance().currentUser?.photoUrl
        Glide.with(binding.imgProfile).load(imgProfile).placeholder(R.drawable.thumbnail)
            .into(binding.imgProfile)
        binding.btnAdd.setOnClickListener {
            openDialogAdd(viewModelLib)
        }
        binding.tvFilter.setOnClickListener {
            popupMenu.show()
        }
        binding.btnChangeLayout.setOnClickListener {
            changeViewType()
        }
        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = false
        }
        return binding.root
    }

    private fun changeUI(b: Boolean) {
        if (b){
            binding.rcvLib.visibility = View.INVISIBLE
            binding.layoutEmpty.visibility = View.VISIBLE
        }
        else{
            binding.layoutEmpty.visibility = View.GONE
            binding.rcvLib.visibility = View.VISIBLE
        }
    }

    private fun changeViewType() {
        if (gridViewEnable){
            gridViewEnable = false
            binding.rcvLib.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter.changeLayoutManager(LINEAR_VIEW)
            binding.btnChangeLayout.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_grid_view_24))
        }
        else {
            gridViewEnable = true
            binding.rcvLib.layoutManager = GridLayoutManager(requireContext(),2)
            adapter.changeLayoutManager(GRID_VIEW)
            binding.btnChangeLayout.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_format_list_bulleted_24))
        }
    }

    private fun openDialogAdd(viewModel: LibraryViewModel) {
        val dialog = BottomSheetDialog(requireContext())
        val bindingBottom: BottomSheetAddPlaylistBinding =
            BottomSheetAddPlaylistBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bindingBottom.root)
        bindingBottom.btnCancel.setOnClickListener { dialog.dismiss() }
        bindingBottom.btnAddPlaylist.setOnClickListener {
            val namePlaylist = bindingBottom.edtName.text.toString().trim()
            dialog.dismiss()
            if (namePlaylist.isNotEmpty()) {
                addPlaylistToLib(namePlaylist, viewModel)
            } else Toast.makeText(requireContext(), "Tên Playlist còn trống!", Toast.LENGTH_SHORT)
                .show()
        }
        dialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun addPlaylistToLib(namePlaylist: String, viewModel: LibraryViewModel) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid.toString()
        val timeStamp = System.currentTimeMillis().toString()
        val calendar = Calendar.getInstance().time
        val dateCreate = SimpleDateFormat("dd-MM-yyyy").format(calendar)
        val playlist = Playlist(timeStamp, namePlaylist, "", user?.displayName, dateCreate, user?.photoUrl.toString(), null, "0", 0, uid)
        listLib.add(playlist)
        viewModel.addPlaylist(listLib,playlist)
        switchFragment(playlist)
    }

    private fun updateRecyclerview(data : Int){
        when(data){
            MY_PLAYLIST->{
                viewModelLib.getPlaylist().observe(viewLifecycleOwner){
                    adapter.setData(it)
                    adapter.notifyDataSetChanged()
                }
            }
            PLAYLIST_FAV ->{
                viewModelLib.getFavPlaylist().observe(viewLifecycleOwner){
                    adapter.setData(it)
                    adapter.notifyDataSetChanged()
                }
            }
            SONG_FAV ->{
                viewModelLib.getFavSong().observe(viewLifecycleOwner){
                    adapter.setData(it)
                    adapter.notifyDataSetChanged()
                }
            }
            ARTIST_FAV ->{
                viewModelLib.getFavArtist().observe(viewLifecycleOwner){
                    adapter.setData(it)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun switchFragment(playlist: Playlist) {
        val addSongPlaylistFragment = AddSongPlaylistFragment()
        if (activity is MainActivity) {
            val activity = activity as MainActivity
            activity.switchFragment(addSongPlaylistFragment, playlist)
        }
    }

    override fun onArtistClick(artist: Artist) {
        val artistDetailFragment = ArtistDetailFragment()
        if (activity is MainActivity) {
            val activity = activity as MainActivity
            activity.switchFragment(artistDetailFragment, artist)
        }
    }
    override fun clickPlaylist(playlist: Playlist, type: Int) {
        if (type == LibraryAdapter.MY_PLAYLIST){
            val myPlaylistFragment = MyPlaylistFragment()
            if (activity is MainActivity) {
                val activity = activity as MainActivity
                activity.switchFragment(myPlaylistFragment, playlist)
            }
        }
        else{
            val playlistDetailFragment = PlaylistDetailFragment()
            if (activity is MainActivity) {
                val activity = activity as MainActivity
                activity.switchFragment(playlistDetailFragment, playlist)
            }
        }
    }

    override fun pickImageToChange(playlist: Playlist) {
        val dialog = BottomSheetDialog(requireContext())
        val bindingBottom = BottomSheetUpdatePlaylistBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bindingBottom.root)
        Glide.with(bindingBottom.imgPlaylist).load(playlist.thumbnail)
            .placeholder(R.drawable.thumbnail).into(bindingBottom.imgPlaylist)
        bindingBottom.tvNamePlaylist.text = playlist.name
        bindingBottom.tvOwner.text = playlist.owner
        bindingBottom.tvDelete.setOnClickListener {
            openAlertDialog(playlist,dialog)
        }
        bindingBottom.tvEdit.setOnClickListener {
            val bsEdit = BottomSheetEditFragment(playlist,viewModelLib,listLib,dialog)
            fragmentManager?.let { it1 -> bsEdit.show(it1,bsEdit.tag) }
        }
        dialog.show()
    }

    private fun openAlertDialog(playlist: Playlist, dialogParent: BottomSheetDialog) {
       val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val binding = AlertSubmitDeleteBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        binding.btnSubmit.setOnClickListener {
            dialog.dismiss()
            dialogParent.dismiss()
            viewModelLib.removePlaylist(playlist)
        }
        binding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}