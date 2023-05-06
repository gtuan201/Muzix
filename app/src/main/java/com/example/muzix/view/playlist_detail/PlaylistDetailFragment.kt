package com.example.muzix.view.playlist_detail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.muzix.R
import com.example.muzix.databinding.FragmentPlaylistDetailBinding
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.example.muzix.viewmodel.PlaylistViewModel
import com.example.muzix.viewmodel.SongViewModel
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import kotlin.math.abs

class PlaylistDetailFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistDetailBinding
    private var playlist : Playlist? = null
    private lateinit var adapter: SongAdapter
    private var mContext : Context? = null

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
        if (bundle != null){
            playlist = bundle.getParcelable("playlist")
        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistDetailBinding.inflate(LayoutInflater.from(mContext),container,false)
        adapter = SongAdapter(requireContext())
        binding.rcvSong.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false)
        binding.rcvSong.setHasFixedSize(true)
        binding.rcvSong.adapter = adapter
        val viewModel = ViewModelProvider(this)[SongViewModel::class.java]
        viewModel.getSong(playlist?.id.toString()).observe(requireActivity()){
            adapter.setData(it)
            adapter.notifyDataSetChanged()
            updateUI()
        }
        val viewModelGlobal = ViewModelProvider(requireActivity())[PlaylistViewModel::class.java]
        val currentSong = viewModelGlobal.currentSong
        if (currentSong != null && currentSong.idPlaylist == playlist?.id){
            adapter.setPlayingPosition(currentSong)
        }
        viewModelGlobal.getCurrentSong().observe(requireActivity()){
            if (it.idPlaylist == playlist?.id){
                adapter.setPlayingPosition(it)
            }
        }
        binding.collapsingToolbar.title = playlist?.name
        binding.tvDescriptionPlaylist.text = playlist?.description
        binding.tvOwner.text = playlist?.owner
        binding.tvLoverDuration.text = "${playlist?.lover} lượt thích · ${playlist?.duration}"
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = abs(verticalOffset) == appBarLayout.totalScrollRange
            binding.imageView.visibility = if (isCollapsed) View.GONE else View.VISIBLE
        }

        Glide.with(this).load(playlist?.thumbnail).into(binding.imageView)
        binding.collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(requireContext(),R.color.white))
        binding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(requireContext(),R.color.white))
        val multiTransformation = MultiTransformation(
            BlurTransformation(50),
            ColorFilterTransformation(Color.argb(80, 0, 0, 0))
        )
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
                        binding.collapsingToolbar.setContentScrimColor(darkColor)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Xử lý khi ảnh không tải được

                }
            })
        // Background Image
        Glide.with(this).asBitmap().load(playlist?.thumbnail)
            .apply(RequestOptions.bitmapTransform(multiTransformation))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val drawable = BitmapDrawable(resources, resource)
                    binding.collapsingToolbar.background = drawable
                }
                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        return binding.root
    }

    private fun updateUI() {
        binding.progressLoading.visibility = View.GONE
        binding.scroll.visibility = View.VISIBLE
        binding.fap.visibility = View.VISIBLE
        binding.appBarLayout.visibility = View.VISIBLE
    }
}