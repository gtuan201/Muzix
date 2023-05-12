package com.example.muzix.view.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.databinding.ItemSongSearchBinding
import com.example.muzix.model.Song
import com.example.muzix.ultis.Constants
import com.example.muzix.ultis.PlayReceiver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val context = holder.itemView.context
        Glide.with(holder.itemView.context).load(song.image).placeholder(R.drawable.thumbnail).into(holder.binding.imgSongSearch)
        holder.binding.tvNameSong.text = song.name
        holder.binding.tvArtist.text = song.artist
        holder.itemView.setOnClickListener{
            val intent = Intent(context,PlayReceiver::class.java)
            intent.putExtra("key",song)
            context?.sendBroadcast(intent)
        }
    }
    fun setData(list : List<Song>){
        listSong = list
    }
}