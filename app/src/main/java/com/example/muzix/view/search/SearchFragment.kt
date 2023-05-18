package com.example.muzix.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.muzix.R
import com.example.muzix.databinding.FragmentSearchBinding
import com.example.muzix.ultis.hiddenSoftKeyboard
import com.example.muzix.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment(){


    private lateinit var binding : FragmentSearchBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(LayoutInflater.from(context),container,false)
        val viewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
//        adapter = SearchAdapter(this@SearchFragment)
//        binding.rcvCategory.layoutManager = GridLayoutManager(requireContext(),2)
//        binding.rcvCategory.adapter = adapter
//        binding.rcvCategory.setHasFixedSize(true)
//        viewModel.getCategory().observe(viewLifecycleOwner){
//            adapter.setData(it)
//            adapter.notifyDataSetChanged()
//        }
        setUpTablayout()
        viewLifecycleOwner.lifecycleScope.launch {
            binding.edtSearch.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateUI(s!!.isNotEmpty())
                    viewModel.setDataSearch(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }
       return binding.root
    }

    private fun updateUI(hasFocus: Boolean) {
        if (hasFocus) {
            binding.layoutFirstSearch.visibility = View.GONE
            binding.tabSearch.visibility = View.VISIBLE
            binding.containerChild.visibility = View.VISIBLE
        }
        else {
            binding.tabSearch.check(R.id.tab_playlist)
            binding.layoutFirstSearch.visibility = View.VISIBLE
            binding.tabSearch.visibility = View.INVISIBLE
            binding.containerChild.visibility = View.INVISIBLE
        }
    }

    private fun setUpTablayout() {
        val ft = childFragmentManager.beginTransaction()
        val playlistSearchFragment = PlaylistSearchFragment()
        val songSearchFragment = SongSearchFragment()
        val artistSearchFragment = ArtistSearchFragment()
        ft.replace(R.id.container_child,PlaylistSearchFragment()).commit()
        binding.tabSearch.check(R.id.tab_playlist)
        binding.tabSearch.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.tab_playlist -> switchFragment(playlistSearchFragment)
                R.id.tab_song -> switchFragment(songSearchFragment)
                R.id.tab_artist -> switchFragment(artistSearchFragment)
            }
        }
    }
    private fun switchFragment(fragment: Fragment){
        activity?.let { hiddenSoftKeyboard(it) }
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.container_child,fragment).commit()
    }

//    override fun onItemClick(category: Category) {
//        val categoryDetailFragment = DetailCategoryFragment()
//        if (activity is MainActivity){
//            val activity = activity as MainActivity
//            activity.switchFragment(categoryDetailFragment,category)
//        }
//    }
}