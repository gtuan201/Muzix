package com.example.muzix.view.playlist_detail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.muzix.R
import com.example.muzix.databinding.ActivityPlaylistDetailBinding
import com.example.muzix.model.Playlist
import com.example.muzix.viewmodel.SongViewModel
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import kotlin.math.abs


class PlaylistDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailBinding
    private var playlist : Playlist? = null
    private lateinit var adapter: SongAdapter
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel = ViewModelProvider(this)[SongViewModel::class.java]
        playlist = intent.getParcelableExtra("playlist")
        viewModel.getSong(playlist?.id.toString()).observe(this){
            adapter = SongAdapter()
            binding.rcvSong.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            binding.rcvSong.setHasFixedSize(true)
            adapter.setData(it)
            binding.rcvSong.adapter = adapter
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
        binding.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
        binding.collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
        val bitmap = (binding.imageView.drawable as? BitmapDrawable)?.bitmap
        if (bitmap != null && !bitmap.isRecycled) {
            Palette.from(bitmap).generate { palette ->
                val darkMutedColor = palette?.getDarkMutedColor(ContextCompat.getColor(this,R.color.main_background))
                binding.collapsingToolbar.setContentScrimColor(darkMutedColor!!)
            }
        }
        val multiTransformation = MultiTransformation(
            BlurTransformation(50),
            ColorFilterTransformation(Color.argb(80, 0, 0, 0))
        )
        Glide.with(this).asBitmap().load(playlist?.thumbnail)
            .apply(RequestOptions.bitmapTransform(multiTransformation))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val drawable = BitmapDrawable(resources, resource)
                    binding.collapsingToolbar.background = drawable
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.e("onLoadCleared","error")
                }
            })
    }
}