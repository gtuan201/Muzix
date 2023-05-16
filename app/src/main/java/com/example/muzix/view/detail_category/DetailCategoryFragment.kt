package com.example.muzix.view.detail_category

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.muzix.R
import com.example.muzix.databinding.FragmentDetailCategoryBinding
import com.example.muzix.databinding.FragmentPlaylistDetailBinding
import com.example.muzix.model.Category
import com.example.muzix.model.Playlist
import com.example.muzix.ultis.OnItemClickListener
import com.example.muzix.view.home.HomeChildAdapter
import com.example.muzix.view.main.MainActivity
import com.example.muzix.view.playlist_detail.PlaylistDetailFragment
import com.example.muzix.viewmodel.PlaylistViewModel


class DetailCategoryFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentDetailCategoryBinding
    private lateinit var adapter: CategoryParentAdapter
    private var category : Category? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null){
            category = bundle.getParcelable("category")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailCategoryBinding.inflate(LayoutInflater.from(context),container,false)
        binding.rcvParentPlaylist.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        adapter = CategoryParentAdapter(this@DetailCategoryFragment)
        val viewModel = ViewModelProvider(requireActivity())[PlaylistViewModel::class.java]
        viewModel.getCollection(category?.id.toString()).observe(requireActivity()){
            adapter.setDataCollection(it)
            binding.rcvParentPlaylist.adapter = adapter
            adapter.notifyDataSetChanged()
            updateUI()
        }
        binding.collapsingToolbar.title = category?.name
        binding.collapsingToolbar.setCollapsedTitleTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        val boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        binding.collapsingToolbar.setExpandedTitleTypeface(boldTypeface)
        binding.collapsingToolbar.expandedTitleTextSize = 54f
        binding.collapsingToolbar.setExpandedTitleColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        return binding.root
    }

    private fun updateUI() {
        Handler().postDelayed({
            binding.progressLoading.visibility = View.GONE
            binding.appBarLayout.visibility = View.VISIBLE
            binding.nestedRcv.visibility = View.VISIBLE
        },1000)
    }

    override fun onItemClick(playlist: Playlist) {
        val playlistDetailFragment = PlaylistDetailFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(playlistDetailFragment,playlist)
        }
    }
}