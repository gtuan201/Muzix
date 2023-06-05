package com.example.muzix.view.library

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.muzix.R
import com.example.muzix.databinding.BottomSheetOptionsBinding
import com.example.muzix.databinding.FragmentMyPlaylistBinding
import com.example.muzix.listener.ClickMoreOptions
import com.example.muzix.listener.ClickToAddSong
import com.example.muzix.model.Favourite
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.example.muzix.service.PlayMusicService
import com.example.muzix.ultis.Constants
import com.example.muzix.ultis.PlayReceiver
import com.example.muzix.ultis.sendActionToService
import com.example.muzix.view.library.AddSongPlaylistFragment.Companion.ACTION_ADD
import com.example.muzix.view.playlist_detail.SongAdapter
import com.example.muzix.viewmodel.FavouriteViewModel
import com.example.muzix.viewmodel.LibraryViewModel
import com.example.muzix.viewmodel.PlaylistViewModel
import com.example.muzix.viewmodel.SongViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlin.math.abs
import kotlin.random.Random

@SuppressLint("NotifyDataSetChanged")
class MyPlaylistFragment : Fragment(), ClickToAddSong, ClickMoreOptions {


    private lateinit var binding : FragmentMyPlaylistBinding
    private var playlist : Playlist? = null
    private lateinit var adapter: SongAdapter
    private lateinit var adapterSuggestAdapter: SongSuggestAdapter
    private var viewModel : SongViewModel? = null
    private var mContext: Context? = null
    private var isPlaying : Boolean = false
    private lateinit var viewModelFav : FavouriteViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlist = it.getParcelable("playlist")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyPlaylistBinding.inflate(LayoutInflater.from(mContext),container,false)
        viewModel = ViewModelProvider(this)[SongViewModel::class.java]
        viewModelFav = ViewModelProvider(requireActivity())[FavouriteViewModel::class.java]
        val viewModelGlobal = ViewModelProvider(requireActivity())[PlaylistViewModel::class.java]
        setUpRcv()
        playlist?.tracks?.let { adapter.setData(it) }
        adapter.notifyDataSetChanged()

        //information my playlist
        binding.collapsingToolbar.title = playlist?.name
        binding.tvOwner.text = playlist?.owner
        binding.tvDuration.text = playlist?.duration
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = abs(verticalOffset) == appBarLayout.totalScrollRange
            binding.imageView.visibility = if (isCollapsed) View.GONE else View.VISIBLE
        }

        //data recyclerview
        getSuggestSong(viewModel!!)
        getAddedSong(viewModel!!)

        // update UI when playing song
        var currentSong = viewModelGlobal.currentSong
        if (currentSong != null && playlist?.tracks != null && playlist?.tracks!!.contains(currentSong)) {
            adapter.setPlayingPosition(currentSong)
        }
        viewModelGlobal.getCurrentSong().observe(requireActivity()) {
            currentSong = it
            if (it != null && playlist?.tracks != null && playlist?.tracks!!.contains(it)) {
                adapter.setPlayingPosition(it)
            } else {
                adapter.setPlayingPosition(null)
                binding.fap.setImageResource(R.drawable.baseline_play_arrow_24)
            }
        }
        viewModelGlobal.getIsPlaying().observe(requireActivity()){
            if (currentSong != null){
                isPlaying = it
                if (isPlaying){
                    binding.fap.setImageResource(R.drawable.baseline_pause_24)
                }
                else{
                    binding.fap.setImageResource(R.drawable.baseline_play_arrow_24)
                }
            }
        }
        isPlaying = viewModelGlobal.isPlaying
        if (isPlaying){ binding.fap.setImageResource(R.drawable.baseline_pause_24) }
        else binding.fap.setImageResource(R.drawable.baseline_play_arrow_24)

        // Color text appbar
        Glide.with(this).load(playlist?.thumbnail).into(binding.imageView)
        binding.collapsingToolbar.setCollapsedTitleTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.collapsingToolbar.setExpandedTitleColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        // Color ContentScrim
        setContentScrimColor(playlist?.thumbnail)
        binding.fap.setOnClickListener {
            clickFap(currentSong)
        }
        binding.btnRefresh.setOnClickListener { viewModel!!.shuffle() }
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        return binding.root
    }

    private fun setContentScrimColor(thumbnail: String?) {
        Glide.with(this)
            .asBitmap()
            .load(thumbnail)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Trích xuất màu tối nhất từ ảnh bằng Palette
                    Palette.from(resource).generate { palette ->
                        val darkMutedSwatch = palette?.darkMutedSwatch
                        val darkColor = darkMutedSwatch?.rgb ?: Color.TRANSPARENT
                        val colors = intArrayOf(
                            darkColor,
                            ContextCompat.getColor(requireContext(), R.color.main_background)
                        )
                        val gradientDrawable =
                            GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                        binding.collapsingToolbar.background = gradientDrawable
                        binding.collapsingToolbar.setContentScrimColor(darkColor)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Xử lý khi ảnh không tải được

                }
            })

    }
    private fun clickFap(currentSong: Song?) {
        if (currentSong != null && playlist?.tracks != null &&  playlist?.tracks!!.contains(currentSong)){
            if (isPlaying){
                sendActionToService(PlayMusicService.ACTION_PAUSE,requireContext(),requireActivity())
            }
            else {
                sendActionToService(PlayMusicService.ACTION_RESUME,requireContext(),requireActivity())
            }
        }
        else{
            binding.fap.setImageResource(R.drawable.baseline_pause_24)
            isPlaying = true
            playingPlaylist()
        }
    }

    private fun playingPlaylist() {
        if (playlist?.tracks != null){
            val intent = Intent(context, PlayReceiver::class.java)
            val position = Random.nextInt(playlist?.tracks!!.size - 1)
            intent.action = Constants.PLAY
            intent.putParcelableArrayListExtra("playlist", playlist?.tracks)
            intent.putExtra("position",position)
            context?.sendBroadcast(intent)
        }
    }

    private fun setUpRcv() {
        //rcv song
        adapter = SongAdapter(requireContext(),this)
        binding.rcvSong.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.rcvSong.setHasFixedSize(true)
        binding.rcvSong.adapter = adapter

        //rcv song suggest
        adapterSuggestAdapter = SongSuggestAdapter(this)
        binding.rcvRecommend.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false)
        binding.rcvRecommend.adapter = adapterSuggestAdapter
        binding.rcvRecommend.setHasFixedSize(true)
    }
    private fun showSnackBar(song: Song, action : Int) {
        val snackBar = Snackbar.make(binding.root,"Đã thêm ${song.name} vào danh sách phát",
            Snackbar.LENGTH_SHORT)
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
                    append(song.name, StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append(" vào danh sách phát")
                }
            }
            AddSongPlaylistFragment.ACTION_REMOVE -> {
                spannable.apply {
                    append("Đã xóa ")
                    append(song.name, StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append(" khỏi danh sách phát")
                }
            }
        }
        snackBar.setText(spannable)
        snackBar.show()
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
    private fun getSuggestSong(viewModel: SongViewModel) {
        viewModel.getAllSong().observe(viewLifecycleOwner){
            adapterSuggestAdapter.apply {
                var listSuggest : MutableList<Song> = mutableListOf()
                var count = 0
                if (playlist?.tracks != null && playlist?.tracks!!.size !=0){
                    for (i in it){
                        if (playlist?.tracks?.contains(i) == false){
                            listSuggest.add(i)
                            count ++
                        }
                        if (count == 5) break
                    }
                }
                else listSuggest = it.take(5) as MutableList<Song>
                setData(listSuggest)
                notifyDataSetChanged()
            }
            if (it != null)  updateUI()
        }
    }
    private fun getAddedSong(viewModel: SongViewModel){
        if (playlist?.tracks == null) {
            viewModel.setSongPlaylist(mutableListOf())
        }
        else viewModel.setSongPlaylist(playlist?.tracks)
        viewModel.getSongAdded().observe(viewLifecycleOwner){
            adapter.apply {
                setData(it as ArrayList<Song>)
                notifyDataSetChanged()
            }
        }
    }

    private fun updateUI() {
        Handler().postDelayed({
            binding.progressLoading.visibility = View.GONE
            binding.appBarLayout.visibility = View.VISIBLE
            binding.scroll.visibility = View.VISIBLE
            binding.fap.visibility = View.VISIBLE
        },800)
    }

    override fun clickMore(song: Song) {
        openOptionsDialog(song)
    }

    private fun openOptionsDialog(song: Song) {
        var isFav = false
        var favourite : Favourite? = null
        val dialog = BottomSheetDialog(requireContext())
        val binding = BottomSheetOptionsBinding.inflate(LayoutInflater.from(context))
        viewModelFav.getFavFromId(song).observe(viewLifecycleOwner){
            isFav = it != null
            favourite = it
            if (isFav){
                setDrawable(binding,true)
            }
            else setDrawable(binding,false)
        }
        dialog.setContentView(binding.root)
        Glide.with(binding.imgSong).load(song.image).placeholder(R.drawable.thumbnail).into(binding.imgSong)
        binding.tvNameSong.text = song.name
        binding.tvArtist.text = song.artist
        binding.tvRemoveSong.setOnClickListener{removeSong(song,dialog)}
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
        showSnackBar(song, AddSongPlaylistFragment.ACTION_REMOVE)
        viewModel!!.backUpSongAll(song)
        viewModel!!.removeSongAdded(song,playlist)
    }
}