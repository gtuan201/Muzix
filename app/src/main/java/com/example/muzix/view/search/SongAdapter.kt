package com.example.muzix.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemSongSearchBinding
import com.example.muzix.model.Song

class SongAdapter : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var listSong : List<Song> = listOf()
    class SongViewHolder(val binding: ItemSongSearchBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
       val song = listSong[position]
        Glide.with(holder.itemView.context).load(song.image).placeholder(R.drawable.thumbnail).into(holder.binding.imgSongSearch)
        holder.binding.tvNameSong.text = song.name
        holder.binding.tvArtist.text = song.artist
    }
    fun setData(list : List<Song>){
        listSong = list
    }
}