package com.example.muzix.ultis

import com.example.muzix.model.Playlist
import com.example.muzix.model.Song

interface OnItemClickListener {
    fun onItemClick(playlist: Playlist)
}