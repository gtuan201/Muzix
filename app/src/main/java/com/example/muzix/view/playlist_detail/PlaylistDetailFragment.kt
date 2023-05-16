package com.example.muzix.view.playlist_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.muzix.R
import com.example.muzix.databinding.FragmentPlaylistDetailBinding
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.example.muzix.service.PlayMusicService
import com.example.muzix.service.PlayMusicService.Companion.ACTION_PAUSE
import com.example.muzix.service.PlayMusicService.Companion.ACTION_RESUME
import com.example.muzix.ultis.Constants
import com.example.muzix.ultis.OnItemClickListener
import com.example.muzix.ultis.PlayReceiver
import com.example.muzix.ultis.sendActionToService
import com.example.muzix.view.home.HomeChildAdapter
import com.example.muzix.view.main.MainActivity
import com.example.muzix.viewmodel.PlaylistViewModel
import com.example.muzix.viewmodel.SongViewModel
import kotlin.math.abs
import kotlin.random.Random

class PlaylistDetailFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentPlaylistDetailBinding
    private var playlist: Playlist? = null
    private var isPlaying : Boolean = false
    private lateinit var adapter: SongAdapter
    private lateinit var playlistAdapter: HomeChildAdapter
    private var listSong : ArrayList<Song> = arrayListOf()
    private var mContext: Context? = null

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
        val bundle = arguments
        if (bundle != null) {
            playlist = bundle.getParcelable("playlist")
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistDetailBinding.inflate(LayoutInflater.from(mContext), container, false)
        setUpRcv()
        // ViewModel getData
        val viewModel = ViewModelProvider(this)[SongViewModel::class.java]
        val viewModelGlobal = ViewModelProvider(requireActivity())[PlaylistViewModel::class.java]
        viewModel.getSong(playlist?.id.toString()).observe(requireActivity()) {
            listSong = it
            adapter.setData(it)
            adapter.notifyDataSetChanged()
            updateUI()
        }
        viewModelGlobal.getRandomPlaylist().observe(requireActivity()){
            playlistAdapter.setDataPlaylist(it)
            adapter.notifyDataSetChanged()
        }
        // update UI when playing song
        var currentSong = viewModelGlobal.currentSong
        if (currentSong != null && currentSong.idPlaylist == playlist?.id) {
            adapter.setPlayingPosition(currentSong)
        }
        viewModelGlobal.getCurrentSong().observe(requireActivity()) {
            currentSong = it
            if (it != null && it.idPlaylist == playlist?.id) {
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
        // Information Playlist
        binding.collapsingToolbar.title = playlist?.name
        binding.tvDescriptionPlaylist.text = playlist?.description
        binding.tvOwner.text = playlist?.owner
        binding.tvLoverDuration.text = "${playlist?.lover} lượt thích · ${playlist?.duration}"
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = abs(verticalOffset) == appBarLayout.totalScrollRange
            binding.imageView.visibility = if (isCollapsed) View.GONE else View.VISIBLE
        }

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
        //click fap
        binding.fap.setOnClickListener {
            if (currentSong != null && currentSong?.idPlaylist == playlist?.id){
                if (isPlaying){
                    sendActionToService(ACTION_PAUSE,requireContext(),requireActivity())
                }
                else {
                    sendActionToService(ACTION_RESUME,requireContext(),requireActivity())
                }
            }
            else{
                binding.fap.setImageResource(R.drawable.baseline_pause_24)
                isPlaying = true
                playingPlaylist()
            }
        }

        // Color ContentScrim
        Glide.with(this)
            .asBitmap()
            .load(playlist?.thumbnail)
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
        return binding.root
    }

    private fun setUpRcv() {
        adapter = SongAdapter(requireContext())
        playlistAdapter = HomeChildAdapter(this@PlaylistDetailFragment)
        binding.rcvSong.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.rcvSong.setHasFixedSize(true)
        binding.rcvSong.adapter = adapter
        binding.rcvRecommend.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.rcvRecommend.adapter = playlistAdapter
        binding.rcvRecommend.setHasFixedSize(true)
    }

    private fun playingPlaylist() {
        val intent = Intent(context, PlayReceiver::class.java)
        val position = Random.nextInt(listSong.size - 1)
        intent.action = Constants.PLAY
        intent.putParcelableArrayListExtra("playlist", listSong)
        intent.putExtra("position",position)
        context?.sendBroadcast(intent)
    }

    private fun updateUI() {
        binding.progressLoading.visibility = View.GONE
        binding.scroll.visibility = View.VISIBLE
        binding.fap.visibility = View.VISIBLE
        binding.appBarLayout.visibility = View.VISIBLE
    }

    override fun onItemClick(playlist: Playlist) {
        val playlistDetailFragment = PlaylistDetailFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(playlistDetailFragment,playlist)
        }
    }
}