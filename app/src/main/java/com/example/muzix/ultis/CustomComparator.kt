package com.example.muzix.ultis

import com.example.muzix.model.Artist
import com.example.muzix.model.Playlist

class CustomComparator : Comparator<Any> {
    override fun compare(o1: Any, o2: Any): Int {
        return when {
            o1 is Playlist && o2 is Playlist -> o1.name.toString().compareTo(o2.name.toString())
            o1 is Artist && o2 is Artist -> o1.name.toString().compareTo(o2.name.toString())
            o1 is Playlist && o2 is Artist -> o1.name.toString().compareTo(o2.name.toString())
            o1 is Artist && o2 is Playlist -> o1.name.toString().compareTo(o2.name.toString())
            else -> 0
        }
    }
}