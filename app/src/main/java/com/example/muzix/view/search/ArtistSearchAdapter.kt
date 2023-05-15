package com.example.muzix.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemArtistSearchBinding
import com.example.muzix.model.Artist
import com.example.muzix.ultis.OnArtistClick
import com.example.muzix.view.home.ArtistAdapter

class ArtistSearchAdapter(private val listener : OnArtistClick) : RecyclerView.Adapter<ArtistSearchAdapter.ArtistSearchVH>() {

    private var listArtist : List<Artist> = listOf()
    class ArtistSearchVH(val binding : ItemArtistSearchBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistSearchVH {
        val binding = ItemArtistSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtistSearchVH(binding)
    }

    override fun getItemCount(): Int {
        return listArtist.size
    }

    override fun onBindViewHolder(holder: ArtistSearchVH, position: Int) {
        val artist = listArtist[position]
        Glide.with(holder.itemView.context).load(artist.image).placeholder(R.drawable.thumbnail).into(holder.binding.imgArtist)
        holder.binding.tvNameArtist.text = artist.name
        holder.itemView.setOnClickListener{
            listener.onArtistClick(artist)
        }
    }
    fun setData(list: List<Artist>){
        listArtist = list
    }
}