package com.example.muzix.view.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.databinding.ItemArtistBinding
import com.example.muzix.model.Artist
import com.example.muzix.ultis.OnArtistClick

class ArtistAdapter(private var list: List<Artist>,val listener : OnArtistClick) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    class ArtistViewHolder(val binding: ItemArtistBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = list[position]
        Glide.with(holder.binding.imgArtist).load(artist.image).into(holder.binding.imgArtist)
        holder.binding.tvNameArtist.text = artist.name
        holder.itemView.setOnClickListener{
            listener.onArtistClick(artist)
        }
    }
}