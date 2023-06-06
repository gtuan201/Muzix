package com.example.muzix.view.library

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemArtistLibBinding
import com.example.muzix.databinding.ItemArtistSearchBinding
import com.example.muzix.databinding.ItemPlaylistLibBinding
import com.example.muzix.databinding.ItemSongSearchBinding
import com.example.muzix.listener.ClickPlaylist
import com.example.muzix.listener.LongClickToChangeImg
import com.example.muzix.model.Artist
import com.example.muzix.model.Playlist
import com.example.muzix.listener.OnArtistClick
import com.example.muzix.model.Song
import com.example.muzix.ultis.PlayReceiver
import com.example.muzix.view.library.LibraryFragment.Companion.GRID_VIEW
import com.google.firebase.auth.FirebaseAuth
@SuppressLint("NotifyDataSetChanged")
class LibraryAdapter(
    private val clickPlaylist: ClickPlaylist,
    private val clickArtist: OnArtistClick,
    private val longClickToChangeImg: LongClickToChangeImg
) : RecyclerView.Adapter<ViewHolder>() {
    companion object {
        const val VIEW_TYPE_PLAYLIST = 0
        const val VIEW_TYPE_ARTIST = 1
        const val VIEW_TYPE_SONG = 2
        const val VIEW_TYPE_ARTIST_LINEAR = 3
        const val VIEW_TYPE_PLAYLIST_LINEAR = 4
        const val VIEW_TYPE_SONG_LINEAR = 5
        const val MY_PLAYLIST = 99
        const val PLAYLIST_FAV = 100
    }

    private var listItem : List<Any> = listOf()
    private var isGridView : Boolean = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PLAYLIST -> {
                val binding = ItemPlaylistLibBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PlaylistViewHolder(binding)
            }
            VIEW_TYPE_ARTIST -> {
                val binding =
                    ItemArtistLibBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ArtistViewHolder(binding)
            }
            VIEW_TYPE_SONG -> {
                val binding = ItemPlaylistLibBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                SongViewHolder(binding)
            }
            VIEW_TYPE_PLAYLIST_LINEAR -> {
                val binding = ItemSongSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                PlaylistVHLinear(binding)
            }
            VIEW_TYPE_ARTIST_LINEAR -> {
                val binding = ItemArtistSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                ArtistVHLinear(binding)
            }
            VIEW_TYPE_SONG_LINEAR -> {
                val binding = ItemSongSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                SongVHLinear(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val context = holder.itemView.context
        when (holder.itemViewType) {
            VIEW_TYPE_PLAYLIST -> {
                (holder as PlaylistViewHolder).bind(listItem[position] as Playlist)
                holder.itemView.setOnClickListener {
                    if ((listItem[position] as Playlist).idCollection == uid) {
                        clickPlaylist.clickPlaylist((listItem[position] as Playlist), MY_PLAYLIST)
                    } else {
                        clickPlaylist.clickPlaylist((listItem[position] as Playlist), PLAYLIST_FAV)
                    }
                }
                holder.itemView.setOnLongClickListener {
                    if ((listItem[position] as Playlist).idCollection == uid)
                        longClickToChangeImg.pickImageToChange((listItem[position] as Playlist))
                    true
                }
            }
            VIEW_TYPE_ARTIST -> {
                (holder as ArtistViewHolder).bind(listItem[position] as Artist)
                holder.itemView.setOnClickListener {
                    clickArtist.onArtistClick(listItem[position] as Artist)
                }
            }
            VIEW_TYPE_SONG -> {
                (holder as SongViewHolder).bind(listItem[position] as Song)
                holder.itemView.setOnClickListener{
                    val intent = Intent(context,PlayReceiver::class.java)
                    val song : Song = listItem[position] as Song
                    intent.putExtra("key",song)
                    context?.sendBroadcast(intent)
                }
            }
            VIEW_TYPE_PLAYLIST_LINEAR -> {
                (holder as PlaylistVHLinear).bind(listItem[position] as Playlist)
                holder.itemView.setOnClickListener {
                    if ((listItem[position] as Playlist).idCollection == uid) {
                        clickPlaylist.clickPlaylist((listItem[position] as Playlist), MY_PLAYLIST)
                    } else {
                        clickPlaylist.clickPlaylist((listItem[position] as Playlist), PLAYLIST_FAV)
                    }
                }
                holder.itemView.setOnLongClickListener {
                    if ((listItem[position] as Playlist).idCollection == uid)
                        longClickToChangeImg.pickImageToChange((listItem[position] as Playlist))
                    true
                }
            }
            VIEW_TYPE_ARTIST_LINEAR -> {
                (holder as ArtistVHLinear).bind(listItem[position] as Artist)
                holder.itemView.setOnClickListener {
                    clickArtist.onArtistClick(listItem[position] as Artist)
                }
            }
            VIEW_TYPE_SONG_LINEAR -> {
                (holder as SongVHLinear).bind(listItem[position] as Song)
                holder.itemView.setOnClickListener{
                    val intent = Intent(context,PlayReceiver::class.java)
                    val song : Song = listItem[position] as Song
                    intent.putExtra("key",song)
                    context?.sendBroadcast(intent)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val objectValue = listItem[position]
        return when {
            objectValue is Playlist && isGridView -> VIEW_TYPE_PLAYLIST
            objectValue is Artist && isGridView -> VIEW_TYPE_ARTIST
            objectValue is Song && isGridView -> VIEW_TYPE_SONG
            objectValue is Playlist && !isGridView -> VIEW_TYPE_PLAYLIST_LINEAR
            objectValue is Artist && !isGridView -> VIEW_TYPE_ARTIST_LINEAR
            objectValue is Song && !isGridView -> VIEW_TYPE_SONG_LINEAR
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

    class SongViewHolder(private val songBinding: ItemPlaylistLibBinding):ViewHolder(songBinding.root){
        fun bind(song : Song){
            Glide.with(songBinding.imgPlaylist).load(song.image).placeholder(R.drawable.thumbnail).into(songBinding.imgPlaylist)
            songBinding.tvNamePlaylist.text = song.name
            songBinding.tvOwner.text = song.artist
        }
    }

    class SongVHLinear(private val binding: ItemSongSearchBinding) : ViewHolder(binding.root){
        fun bind(song: Song){
            Glide.with(binding.imgSongSearch).load(song.image).placeholder(R.drawable.thumbnail).into(binding.imgSongSearch)
            binding.tvNameSong.text = song.name
            binding.tvArtist.text = song.artist
        }
    }

    class ArtistVHLinear(private val binding : ItemArtistSearchBinding) : ViewHolder(binding.root){
        fun bind(artist: Artist){
            Glide.with(binding.imgArtist).load(artist.image).placeholder(R.drawable.thumbnail).into(binding.imgArtist)
            binding.tvNameArtist.text = artist.name
        }
    }

    class PlaylistVHLinear(private val binding: ItemSongSearchBinding ) : ViewHolder(binding.root){
        fun bind(playlist: Playlist){
            Glide.with(binding.imgSongSearch).load(playlist.thumbnail).placeholder(R.drawable.thumbnail).into(binding.imgSongSearch)
            binding.tvNameSong.text = playlist.name
            binding.tvArtist.text = playlist.owner
        }
    }
    fun setData(list: List<Any>){
        listItem = listOf()
        listItem = list
        notifyDataSetChanged()
    }

    fun changeLayoutManager(type : Int){
        isGridView = type == GRID_VIEW
        notifyDataSetChanged()
    }
}