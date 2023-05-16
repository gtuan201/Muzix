package com.example.muzix.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemPlaylistSearchBinding
import com.example.muzix.model.Playlist
import com.example.muzix.ultis.OnItemClickListener
import com.example.muzix.ultis.hiddenSoftKeyboard
import com.example.muzix.view.home.HomeChildAdapter
import com.example.muzix.view.main.MainActivity

class PlaylistAdapter(private val listener : OnItemClickListener) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private var listPlaylist : List<Playlist> = listOf()
    class PlaylistViewHolder(val binding: ItemPlaylistSearchBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listPlaylist.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = listPlaylist[position]
        Glide.with(holder.binding.imgPlaylist).load(playlist.thumbnail).placeholder(R.drawable.thumbnail).into(holder.binding.imgPlaylist)
        holder.binding.tvNamePlaylist.text = playlist.name
        holder.itemView.setOnClickListener{
            listener.onItemClick(playlist)
        }
    }
    fun setData(list: List<Playlist>){
        listPlaylist = list
    }
}