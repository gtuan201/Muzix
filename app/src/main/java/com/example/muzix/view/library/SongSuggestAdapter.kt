package com.example.muzix.view.library

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemSongSuggestBinding
import com.example.muzix.model.Song
import com.example.muzix.listener.ClickToAddSong

class SongSuggestAdapter(private val listener : ClickToAddSong) : RecyclerView.Adapter<SongSuggestAdapter.SongSuggestVH>() {

    private var listSuggest: MutableList<Song> = mutableListOf()
    class SongSuggestVH(val binding : ItemSongSuggestBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongSuggestVH {
        val binding = ItemSongSuggestBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SongSuggestVH(binding)
    }

    override fun getItemCount(): Int {
        return listSuggest.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SongSuggestVH, position: Int) {
        val song = listSuggest[position]
        Glide.with(holder.itemView.context).load(song.image).placeholder(R.drawable.thumbnail).into(holder.binding.imgSong)
        holder.binding.tvNameSong.text = song.name
        holder.binding.tvArtist.text = song.artist
        holder.binding.tvDurationListens.text = "${song.listens}•${song.duration}"
        holder.binding.btnAdd.setOnClickListener { listener.onClickAddSong(song) }
    }
    fun setData(list : List<Song>){
        listSuggest = if (list.isEmpty()) mutableListOf()
        else list as MutableList<Song>
    }
}