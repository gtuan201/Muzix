package com.example.muzix.listener

import com.example.muzix.model.Playlist

interface ClickPlaylist {
    fun clickPlaylist(playlist: Playlist,type : Int)
}