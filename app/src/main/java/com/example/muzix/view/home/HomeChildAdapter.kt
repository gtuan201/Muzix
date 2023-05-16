package com.example.muzix.view.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemHomeChildBinding
import com.example.muzix.model.Playlist
import com.example.muzix.ultis.OnItemClickListener

class HomeChildAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<HomeChildAdapter.HomeChildViewHolder>() {

    private var listPlaylist: List<Playlist> = emptyList()

    class HomeChildViewHolder(val binding: ItemHomeChildBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChildViewHolder {
        val binding =
            ItemHomeChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeChildViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listPlaylist.size
    }

    override fun onBindViewHolder(holder: HomeChildViewHolder, position: Int) {
        val playlist = listPlaylist[position]
        Glide.with(holder.binding.imgPlaylist)
            .load(playlist.thumbnail)
            .placeholder(R.drawable.thumbnail)
            .into(holder.binding.imgPlaylist)
        holder.binding.tvNamePlaylist.text = playlist.name
        holder.binding.tvOwner.text = playlist.owner
        holder.itemView.setOnClickListener {
            listener.onItemClick(playlist)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataPlaylist(list: List<Playlist>) {
        listPlaylist = list
        notifyDataSetChanged()
    }
}