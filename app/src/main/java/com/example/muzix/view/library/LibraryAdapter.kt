package com.example.muzix.view.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemArtistBinding
import com.example.muzix.databinding.ItemArtistLibBinding
import com.example.muzix.databinding.ItemHomeChildBinding
import com.example.muzix.databinding.ItemPlaylistLibBinding
import com.example.muzix.model.Artist
import com.example.muzix.model.Playlist
import com.example.muzix.ultis.OnArtistClick
import com.example.muzix.ultis.OnItemClickListener

class LibraryAdapter(
    private val listItem: List<Any>,
    private val clickPlaylist: OnItemClickListener,
    private val clickArtist: OnArtistClick
) : RecyclerView.Adapter<ViewHolder>() {
    companion object {
        const val VIEW_TYPE_PLAYLIST = 0
        const val VIEW_TYPE_ARTIST = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PLAYLIST -> {
                val binding = ItemPlaylistLibBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PlaylistViewHolder(binding)
            }
            VIEW_TYPE_ARTIST -> {
                val binding =
                    ItemArtistLibBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ArtistViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_PLAYLIST -> {
                (holder as PlaylistViewHolder).bind(listItem[position] as Playlist)
                holder.itemView.setOnClickListener {
                    clickPlaylist.onItemClick(listItem[position] as Playlist)
                }
            }
            VIEW_TYPE_ARTIST -> {
                (holder as ArtistViewHolder).bind(listItem[position] as Artist)
                holder.itemView.setOnClickListener{
                    clickArtist.onArtistClick(listItem[position] as Artist)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItem[position]) {
            is Playlist -> VIEW_TYPE_PLAYLIST
            is Artist -> VIEW_TYPE_ARTIST
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    class PlaylistViewHolder(private val playlistBinding: ItemPlaylistLibBinding) :
        ViewHolder(playlistBinding.root) {
        fun bind(playlist: Playlist) {
            playlistBinding.tvNamePlaylist.text = playlist.name
            Glide.with(playlistBinding.imgPlaylist).load(playlist.thumbnail)
                .placeholder(R.drawable.thumbnail).into(playlistBinding.imgPlaylist)
            playlistBinding.tvOwner.text = playlist.owner
        }
    }

    class ArtistViewHolder(private val artistBinding: ItemArtistLibBinding) :
        ViewHolder(artistBinding.root) {
        fun bind(artist: Artist) {
            artistBinding.tvNameArtist.text = artist.name
            Glide.with(artistBinding.imgArtist).load(artist.image).placeholder(R.drawable.thumbnail)
                .into(artistBinding.imgArtist)
        }
    }
}