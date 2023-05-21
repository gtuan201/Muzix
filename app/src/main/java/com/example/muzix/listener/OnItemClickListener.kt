package com.example.muzix.listener

import com.example.muzix.model.Playlist
import com.example.muzix.model.Song

interface OnItemClickListener {
    fun onItemClick(playlist: Playlist)
}