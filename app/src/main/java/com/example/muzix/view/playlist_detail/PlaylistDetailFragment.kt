package com.example.muzix.view.playlist_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.muzix.R
import com.example.muzix.databinding.BottomSheetOptionsBinding
import com.example.muzix.databinding.FragmentPlaylistDetailBinding
import com.example.muzix.listener.ClickMoreOptions
import com.example.muzix.listener.OnItemClickListener
import com.example.muzix.model.Favourite
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.example.muzix.service.PlayMusicService.Companion.ACTION_PAUSE
import com.example.muzix.service.PlayMusicService.Companion.ACTION_RESUME
import com.example.muzix.ultis.Constants
import com.example.muzix.ultis.PlayReceiver
import com.example.muzix.ultis.sendActionToService
import com.example.muzix.view.home.HomeChildAdapter
import com.example.muzix.view.main.MainActivity
import com.example.muzix.viewmodel.FavouriteViewModel
import com.example.muzix.viewmodel.PlaylistViewModel
import com.example.muzix.viewmodel.SongViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.random.Random

class PlaylistDetailFragment : Fragment(), OnItemClickListener, ClickMoreOptions {

    private lateinit var binding: FragmentPlaylistDetailBinding
    private var playlist: Playlist? = null
    private var isPlaying : Boolean = false
    private lateinit var adapter: SongAdapter
    private lateinit var playlistAdapter: HomeChildAdapter
    private var listSong : ArrayList<Song> = arrayListOf()
    private var isFavourite : Boolean = false
    private var favourite : Favourite? = null
    private lateinit var viewModelFav : FavouriteViewModel
    private var mContext: Context? = null
    companion object {
        private const val ACTION_ADD = 0
        private const val ACTION_REMOVE = 1
    }

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
        viewModelFav = ViewModelProvider(requireActivity())[FavouriteViewModel::class.java]
        viewModel.getSong(playlist?.id.toString()).observe(requireActivity()) {
            listSong = it
            if (playlist?.tracks != null && playlist?.tracks!!.isNotEmpty()){
                listSong.addAll(playlist?.tracks!!)
            }
            setDurationPlaylist(listSong)
            adapter.setData(listSong)
            adapter.notifyDataSetChanged()
            updateUI()
        }
        viewModelGlobal.getRandomPlaylist().observe(requireActivity()){
            playlistAdapter.setDataPlaylist(it)
            adapter.notifyDataSetChanged()
        }
        viewModelFav.getFavFromId(playlist).observe(viewLifecycleOwner){
            isFavourite = it != null
            favourite = it
            if (it != null) {
                binding.btnFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_favorite_24))
            }
            else {
                binding.btnFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_favorite_border_24))
            }
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
                viewModelGlobal.addHistoryPlaylist(playlist!!)
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
        binding.btnFavorite.setOnClickListener {
            if (isFavourite){
                removeFavourite(viewModelFav)
            }
            else {
                addToFavourite(viewModelFav)
            }
        }
        binding.btnDownload.setOnClickListener {
            val snackBar = Snackbar.make(binding.root,"Chức năng chỉ có ở Muzix Premium", Snackbar.LENGTH_SHORT)
            val layoutParams = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0,0,0,110)
            snackBar.view.layoutParams = layoutParams
            snackBar.show()
        }
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        return binding.root
    }


    private fun removeFavourite(viewModelFav: FavouriteViewModel) {
        YoYo.with(Techniques.Wobble)
            .duration(500)
            .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
            .playOn(binding.btnFavorite)
        viewModelFav.removeFavourite(favourite!!).observe(viewLifecycleOwner){
            if(it == null) showSnackBar(ACTION_REMOVE)
        }
        val newPlaylist = playlist?.copy(lover = playlist?.lover?.minus(1))
        viewModelFav.updatePlaylist(newPlaylist)
    }
    private fun addToFavourite(viewModelFav: FavouriteViewModel) {
        YoYo.with(Techniques.Wobble)
            .duration(500)
            .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
            .playOn(binding.btnFavorite)
        viewModelFav.addToFavourite(playlist).observe(viewLifecycleOwner){
           if (it != null) showSnackBar(ACTION_ADD)
        }
        val newPlaylist = playlist?.copy(lover = playlist?.lover?.plus(1))
        viewModelFav.updatePlaylist(newPlaylist)
    }
    private fun showSnackBar(action : Int) {
        val snackBar = Snackbar.make(binding.root,"Đã thêm ${playlist?.name} vào danh sách phát", Snackbar.LENGTH_SHORT)
        val layoutParams = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0,0,0,110)
        snackBar.view.layoutParams = layoutParams
        snackBar.setTextColor(ContextCompat.getColor(requireContext(),R.color.main_background))
        snackBar.setBackgroundTint(Color.WHITE)
        val spannable = SpannableStringBuilder()
        when(action){
            ACTION_ADD -> {
                spannable.apply {
                    append("Đã thêm ")
                    append(playlist?.name, StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append(" vào thư viện")
                }
            }
            ACTION_REMOVE -> {
                spannable.apply {
                    append("Đã xóa ")
                    append(playlist?.name, StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append(" khỏi thư viện")
                }
            }
        }
        snackBar.setText(spannable)
        snackBar.show()
    }

    private fun setUpRcv() {
        adapter = SongAdapter(requireContext(),this)
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
        val position = if (listSong.size != 1){
            Random.nextInt(listSong.size - 1)
        } else 0
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
        binding.tvRemoveSong.visibility = View.GONE
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
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setDurationPlaylist(listSong: ArrayList<Song>?) {
        val calendar = Calendar.getInstance()
        val df = SimpleDateFormat("mm:ss")
        var total : Long = 0
        if (listSong != null){
            for (i in listSong){
                val time = df.parse(i.duration.toString())
                if (time != null) {
                    val durationInMillis = TimeUnit.MILLISECONDS.toMillis(time.time)
                    total += durationInMillis
                }
            }
        }
        val time = Date()
        time.time = total
        calendar.time = time
        val hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        val minute = calendar.get(Calendar.MINUTE).toString()
        val formatter = NumberFormat.getNumberInstance(Locale.GERMAN)
        formatter.isGroupingUsed = true
        val formatted = formatter.format(playlist?.lover ?: 0)
        binding.tvLoverDuration.text = "$formatted lượt thích • $hour giờ $minute phút"
    }
}