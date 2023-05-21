package com.example.muzix.view.playlist_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemSongBinding
import com.example.muzix.model.Song
import com.example.muzix.ultis.ClickRemoveSong
import com.example.muzix.ultis.Constants.Companion.PLAY
import com.example.muzix.ultis.PlayReceiver

class SongAdapter(private val context: Context,private val listener : ClickRemoveSong) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var listSong : ArrayList<Song> = ArrayList()
    private var playingPosition : Song? = null

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
        if (song == playingPosition){
            holder.binding.tvNameSong.setTextColor(context.getColor(R.color.main))
        }
        else holder.binding.tvNameSong.setTextColor(context.getColor(R.color.white))
        Glide.with(holder.binding.imgSong).load(song.image).into(holder.binding.imgSong)
        holder.binding.tvNameSong.text = song.name
        holder.binding.tvSinger.text = song.artist
        holder.binding.tvNumber.text = "${position + 1}"
        holder.itemView.setOnClickListener{
            val intent = Intent(context,PlayReceiver::class.java)
            intent.action = PLAY
            intent.putParcelableArrayListExtra("playlist",listSong)
            intent.putExtra("position",position)
            context.sendBroadcast(intent)
        }
        holder.binding.btnMore.setOnClickListener {listener.clickRemoveSong(song)}
    }
    fun setData(list : ArrayList<Song>){
        listSong = list
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setPlayingPosition(song: Song?){
        playingPosition = song
        notifyDataSetChanged()
    }
}