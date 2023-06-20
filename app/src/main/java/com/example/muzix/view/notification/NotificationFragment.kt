package com.example.muzix.view.notification

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.muzix.R
import com.example.muzix.databinding.FragmentNotificationBinding
import com.example.muzix.listener.ClickNotification
import com.example.muzix.model.Notification
import com.example.muzix.view.main.MainActivity
import com.example.muzix.view.playlist_detail.PlaylistDetailFragment
import com.example.muzix.viewmodel.NotificationViewModel

class NotificationFragment : Fragment(),ClickNotification {

    private lateinit var binding : FragmentNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private lateinit var viewModel : NotificationViewModel
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(LayoutInflater.from(context),container,false)
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        adapter = NotificationAdapter(this)
        binding.rcvNoti.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(dividerDrawable!!)
        binding.rcvNoti.addItemDecoration(dividerItemDecoration)
        binding.rcvNoti.adapter = adapter
        viewModel.getAllNotification(requireContext()).observe(viewLifecycleOwner){
            adapter.setData(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.deleteOldNotification(requireContext())
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed()}
        return binding.root
    }
    override fun clickNoti(notification: Notification) {
        val playlistDetailFragment = PlaylistDetailFragment()
        viewModel.getPlaylist(notification.id_playlist.toString()).observe(viewLifecycleOwner){
            if (activity is MainActivity){
                val activity = activity as MainActivity
                activity.switchFragment(playlistDetailFragment,it)
            }
        }
    }
}