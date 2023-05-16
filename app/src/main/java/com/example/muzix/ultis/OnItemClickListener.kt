package com.example.muzix.ultis

import com.example.muzix.model.Playlist

interface OnItemClickListener {
    fun onItemClick(playlist: Playlist)
}