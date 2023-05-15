package com.example.muzix.view.artist_detail

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.example.muzix.model.Song
import com.example.muzix.ultis.OnArtistClick
import com.example.muzix.view.home.ArtistAdapter
import com.example.muzix.view.main.MainActivity
import com.example.muzix.view.playlist_detail.SongAdapter
import com.example.muzix.viewmodel.ArtistViewModel


class ArtistDetailFragment : Fragment(),OnArtistClick {

    private lateinit var binding: FragmentArtistDetailBinding
    private lateinit var adapter: SongAdapter
    private lateinit var artistAdapter: ArtistAdapter
    private var artist : Artist? = null
    private var alpha: Float = 0f
    private var isExpandable : Boolean = false
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
        binding =
            FragmentArtistDetailBinding.inflate(LayoutInflater.from(context), container, false)
        binding.rcvSong.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = SongAdapter(requireContext())
        binding.rcvSong.adapter = adapter
        binding.rcvFavouriteArtist.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        val viewModel = ViewModelProvider(this)[ArtistViewModel::class.java]
        viewModel.getSongOfArtist(artist?.name!!.lowercase()).observe(viewLifecycleOwner) {
            adapter.setData(it as ArrayList<Song>)
            adapter.notifyDataSetChanged()
        }
        viewModel.getRandomArtist().observe(viewLifecycleOwner){
            artistAdapter = ArtistAdapter(it,this@ArtistDetailFragment)
            binding.rcvFavouriteArtist.adapter = artistAdapter
            adapter.notifyDataSetChanged()
        }
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
        return binding.root
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
}