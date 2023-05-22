package com.example.muzix.view.library

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.BottomSheetOptionsBinding
import com.example.muzix.databinding.FragmentAddSongPlaylistBinding
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.example.muzix.listener.ClickMoreOptions
import com.example.muzix.listener.ClickToAddSong
import com.example.muzix.model.Favourite
import com.example.muzix.viewmodel.FavouriteViewModel
import com.example.muzix.viewmodel.SongViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
class AddSongPlaylistFragment : Fragment(), ClickToAddSong, ClickMoreOptions {

    companion object{
        const val ACTION_ADD = 0
        const val ACTION_REMOVE = 1
    }
    private lateinit var binding : FragmentAddSongPlaylistBinding
    private lateinit var adapterSuggest : SongSuggestAdapter
    private lateinit var adapterAdded : SongAddedAdapter
    private var playlist : Playlist? = null
    private var listSuggest : MutableList<Song> = mutableListOf()
    private var viewModel : SongViewModel? = null
    private lateinit var viewModelFav : FavouriteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           playlist = it.getParcelable("playlist")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddSongPlaylistBinding.inflate(LayoutInflater.from(context),container,false)
        viewModel = ViewModelProvider(this)[SongViewModel::class.java]
        viewModelFav = ViewModelProvider(this)[FavouriteViewModel::class.java]
        setUpRecyclerview()
        val user = FirebaseAuth.getInstance().currentUser
        //display information playlist
        binding.tvNamePlaylist.text = playlist?.name
        Glide.with(this).load(user?.photoUrl).placeholder(R.drawable.thumbnail).into(binding.imgPlaylist)
        Glide.with(this).load(user?.photoUrl).placeholder(R.drawable.thumbnail).into(binding.imgOwner)
        binding.tvNameOwner.text = "By ${user?.displayName}"
        //list song suggest & added
        getSuggestSong(viewModel!!)
        getAddedSong(viewModel!!)
        listSuggest = viewModel!!.listAllSong
        binding.btnRefresh.setOnClickListener { viewModel!!.shuffle() }
        return binding.root
    }

    private fun setUpRecyclerview() {
        adapterSuggest = SongSuggestAdapter(this)
        adapterAdded = SongAddedAdapter(this)
        binding.rcvSongAdded.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rcvSongAdded.adapter = adapterAdded
        binding.rcvSongSuggest.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rcvSongSuggest.adapter = adapterSuggest
    }

    private fun getSuggestSong(viewModel: SongViewModel) {
        viewModel.getAllSong().observe(viewLifecycleOwner){
            adapterSuggest.apply {
                setData(it.take(5))
                notifyDataSetChanged()
            }
        }
    }
    private fun getAddedSong(viewModel: SongViewModel){
        viewModel.getSongAdded().observe(viewLifecycleOwner){
            adapterAdded.apply {
                setData(it)
                notifyDataSetChanged()
            }
        }
    }

    override fun onClickAddSong(song: Song) {
        viewModel!!.deleteSongAll(song)
        viewModel!!.addSongPlaylist(song)
        showSnackBar(song, ACTION_ADD)
        addSongToPlaylist(song)
    }

    private fun addSongToPlaylist(song: Song) {
        viewModel!!.addSongToPlaylist(song,playlist)
    }

    private fun showSnackBar(song: Song, action : Int) {
        val snackBar = Snackbar.make(binding.root,"Đã thêm ${song.name} vào danh sách phát",Snackbar.LENGTH_SHORT)
        val layoutParams = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0,0,0,210)
        snackBar.view.layoutParams = layoutParams
        snackBar.setTextColor(ContextCompat.getColor(requireContext(),R.color.main_background))
        snackBar.setBackgroundTint(Color.WHITE)
        val spannable = SpannableStringBuilder()
        when(action){
            ACTION_ADD -> {
                spannable.apply {
                    append("Đã thêm ")
                    append(song.name,StyleSpan(Typeface.BOLD),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append(" vào danh sách phát")
                }
            }
            ACTION_REMOVE -> {
                spannable.apply {
                    append("Đã xóa ")
                    append(song.name,StyleSpan(Typeface.BOLD),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append(" khỏi danh sách phát")
                }
            }
        }
        snackBar.setText(spannable)
        snackBar.show()
    }

    override fun clickMore(song: Song) {
        openOptionsDialog(song)
    }

    private fun openOptionsDialog(song: Song) {
        var isFav = false
        var favourite : Favourite? = null
        val dialog = BottomSheetDialog(requireContext())
        val binding = BottomSheetOptionsBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        Glide.with(binding.imgSong).load(song.image).placeholder(R.drawable.thumbnail).into(binding.imgSong)
        binding.tvNameSong.text = song.name
        binding.tvArtist.text = song.artist
        binding.tvRemoveSong.setOnClickListener { removeSong(song,dialog) }
        viewModelFav.getFavFromId(song).observe(viewLifecycleOwner){
            isFav = it != null
            favourite = it
            if (isFav) { setDrawable(binding,true) }
            else setDrawable(binding,false)
        }
        binding.tvFavourite.setOnClickListener {
            if (isFav) { viewModelFav.removeFavouriteSong(favourite!!) }
            else { viewModelFav.addToFavourite(song) }
        }
        dialog.show()
    }
    private fun setDrawable(binding: BottomSheetOptionsBinding, isFav: Boolean) {
        val drawable = if (isFav){ ContextCompat.getDrawable(requireContext(),R.drawable.baseline_favorite_24)
        } else ContextCompat.getDrawable(requireContext(),R.drawable.baseline_favorite_border_24)
        if (isFav) { drawable!!.colorFilter = PorterDuffColorFilter(Color.parseColor("#FFD154"), PorterDuff.Mode.SRC_IN) }
        else drawable!!.colorFilter = PorterDuffColorFilter(Color.parseColor("#AEAEAE"), PorterDuff.Mode.SRC_IN)
        binding.tvFavourite.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
    }

    private fun removeSong(song: Song, dialog: BottomSheetDialog) {
        dialog.dismiss()
        showSnackBar(song, ACTION_REMOVE)
        viewModel!!.backUpSongAll(song)
        viewModel!!.removeSongAdded(song,playlist)
    }
}