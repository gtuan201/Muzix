package com.example.muzix.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.muzix.databinding.FragmentSearchBinding
import com.example.muzix.model.Category
import com.example.muzix.ultis.showSoftKeyboard
import com.example.muzix.view.detail_category.DetailCategoryFragment
import com.example.muzix.view.main.MainActivity
import com.example.muzix.view.playlist_detail.PlaylistDetailFragment
import com.example.muzix.viewmodel.CategoryViewModel

class SearchFragment : Fragment(),SearchAdapter.OnCategoryClickListener {


    private lateinit var binding : FragmentSearchBinding
    private lateinit var adapter: SearchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(LayoutInflater.from(context),container,false)
        val viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        adapter = SearchAdapter(this@SearchFragment)
        binding.rcvCategory.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rcvCategory.adapter = adapter
        binding.rcvCategory.setHasFixedSize(true)
        viewModel.getCategory().observe(viewLifecycleOwner){
            adapter.setData(it)
            adapter.notifyDataSetChanged()
        }
        binding.edtSearch.setOnClickListener{

        }
        return binding.root
    }

    override fun onItemClick(category: Category) {
        val categoryDetailFragment = DetailCategoryFragment()
        if (activity is MainActivity){
            val activity = activity as MainActivity
            activity.switchFragment(categoryDetailFragment,category)
        }
    }
}