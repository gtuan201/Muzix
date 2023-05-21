package com.example.muzix.view.library

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemSongAddedBinding
import com.example.muzix.model.Song
import com.example.muzix.listener.ClickMoreOptions

class SongAddedAdapter(private val listener : ClickMoreOptions) : RecyclerView.Adapter<SongAddedAdapter.SongAddedVH>() {

    private var listAdded: MutableList<Song> = mutableListOf()
    class SongAddedVH(val binding : ItemSongAddedBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongAddedVH {
        val binding = ItemSongAddedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SongAddedVH(binding)
    }

    override fun getItemCount(): Int {
        return listAdded.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SongAddedVH, position: Int) {
        val song = listAdded[position]
        Glide.with(holder.itemView.context).load(song.image).placeholder(R.drawable.thumbnail).into(holder.binding.imgSong)
        holder.binding.tvNameSong.text = song.name
        holder.binding.tvArtist.text = song.artist
        holder.binding.tvDurationListens.text = "${song.listens}•${song.duration}"
        holder.binding.btnMore.setOnClickListener { listener.clickMore(song) }
    }
    fun setData(list : List<Song>){
        listAdded = if (list.isEmpty()) mutableListOf()
        else list as MutableList<Song>
    }
}