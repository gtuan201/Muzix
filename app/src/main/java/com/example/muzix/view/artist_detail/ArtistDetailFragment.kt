package com.example.muzix.view.artist_detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.FragmentArtistDetailBinding
import com.example.muzix.model.Artist
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.example.muzix.service.PlayMusicService
import com.example.muzix.service.PlayMusicService.Companion.ACTION_PAUSE
import com.example.muzix.service.PlayMusicService.Companion.ACTION_RESUME
import com.example.muzix.ultis.*
import com.example.muzix.view.home.ArtistAdapter
import com.example.muzix.view.home.HomeChildAdapter
import com.example.muzix.view.main.MainActivity
import com.example.muzix.view.playlist_detail.PlaylistDetailFragment
import com.example.muzix.view.playlist_detail.SongAdapter
import com.example.muzix.view.search.PlaylistAdapter
import com.example.muzix.viewmodel.ArtistViewModel
import com.example.muzix.viewmodel.PlaylistViewModel
import kotlin.random.Random


class ArtistDetailFragment : Fragment(),OnArtistClick, OnItemClickListener,ClickRemoveSong {

    private lateinit var binding: FragmentArtistDetailBinding
    private lateinit var adapter: SongAdapter
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var playlistAdapter: HomeChildAdapter
    private var artist : Artist? = null
    private var alpha: Float = 0f
    private var isExpandable : Boolean = false
    private var isPlaying : Boolean = false
    private var listSong : List<Song> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            artist = bundle.getParcelable("artist")
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentArtistDetailBinding.inflate(LayoutInflater.from(context), container, false)
        setUpRcv()
        //ViewModel get data
        val viewModel = ViewModelProvider(this)[ArtistViewModel::class.java]
        val viewModelGlobal = ViewModelProvider(requireActivity())[PlaylistViewModel::class.java]
        viewModel.getSongOfArtist(artist?.name!!.lowercase()).observe(viewLifecycleOwner) {
            listSong = it
            adapter.setData(it as ArrayList<Song>)
            adapter.notifyDataSetChanged()
            updateUI()
        }
        viewModel.getRandomArtist().observe(viewLifecycleOwner){
            artistAdapter = ArtistAdapter(it,this@ArtistDetailFragment)
            binding.rcvFavouriteArtist.adapter = artistAdapter
            adapter.notifyDataSetChanged()
        }
        viewModel.getPlaylist(artist?.name!!.lowercase()).observe(viewLifecycleOwner){
            playlistAdapter.setDataPlaylist(it)
            playlistAdapter.notifyDataSetChanged()
        }
        // update UI when playing song
        var currentSong = viewModelGlobal.currentSong
        if (currentSong != null && listSong.contains(currentSong)) {
            adapter.setPlayingPosition(currentSong)
        }
        viewModelGlobal.getCurrentSong().observe(requireActivity()) {
            currentSong = it
            if (it != null && listSong.contains(currentSong)) {
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
        // information artist
        Glide.with(requireActivity()).load(artist?.background).into(binding.imgBackgroud)
        Glide.with(requireActivity()).load(artist?.image).placeholder(R.drawable.thumbnail).into(binding.imgArtist)
        binding.tvNameArtist.text = artist?.name
        binding.toolbar.text = artist?.name
        binding.tvNation.text = artist?.nation
        binding.tvFollower.text = "${artist?.lover} Người theo dõi"
        binding.tvDescription.text = artist?.description
        binding.showMore.setOnClickListener{
           showMoreShowLess()
        }
        scrollToShowToolbar()
        //click fap
        binding.fap.setOnClickListener {
            if (currentSong != null && listSong.contains(currentSong)){
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
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        return binding.root
    }

    private fun updateUI() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.nestedScrollView.visibility = View.VISIBLE
            binding.progressLoading.visibility = View.GONE
        },1000)
    }

    private fun setUpRcv() {
        binding.rcvSong.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = SongAdapter(requireContext(),this)
        binding.rcvSong.adapter = adapter
        binding.rcvFavouriteArtist.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.rcvPlaylist.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        playlistAdapter = HomeChildAdapter(this@ArtistDetailFragment)
        binding.rcvPlaylist.adapter = playlistAdapter
    }

    private fun showMoreShowLess() {
        if (isExpandable){
            binding.showMore.text = getString(R.string.show_less)
            binding.tvDescription.maxLines = Int.MAX_VALUE
            isExpandable = false
        }
        else {
            binding.showMore.text = getString(R.string.show_more)
            binding.tvDescription.maxLines = 2
            isExpandable = true
        }
    }

    private fun scrollToShowToolbar() {
        binding.toolbar.alpha = 0f
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            alpha = if (scrollY <= 140)
                scrollY.toFloat()/140f
            else 1f
            binding.toolbar.alpha = alpha
        }
    }

    override fun onArtistClick(artist: Artist) {
        val artistDetailFragment = ArtistDetailFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(artistDetailFragment,artist)
        }
    }

    override fun onItemClick(playlist: Playlist) {
        val playlistDetailFragment = PlaylistDetailFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(playlistDetailFragment,playlist)
        }
    }
    private fun playingPlaylist() {
        val intent = Intent(context, PlayReceiver::class.java)
        val position = Random.nextInt(listSong.size - 1)
        intent.action = Constants.PLAY
        intent.putParcelableArrayListExtra("playlist", listSong as ArrayList)
        intent.putExtra("position",position)
        context?.sendBroadcast(intent)
    }

    override fun clickRemoveSong(song: Song) {

    }
}