package com.example.muzix.view.playlist_detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.databinding.ItemSongBinding
import com.example.muzix.model.Song

class SongAdapter : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var listSong : List<Song> = ArrayList()
    class SongViewHolder(val binding: ItemSongBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
       val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = listSong[position]
        Glide.with(holder.binding.imgSong).load(song.image).into(holder.binding.imgSong)
        holder.binding.tvNameSong.text = song.name
        holder.binding.tvSinger.text = song.artist
        holder.binding.tvNumber.text = "${position + 1}"
    }
    fun setData(list : ArrayList<Song>){
        listSong = list
    }
}