package com.example.muzix.listener

import com.example.muzix.model.Song

interface ClickToAddSong {
    fun onClickAddSong(song: Song)
}