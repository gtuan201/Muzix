package com.example.muzix.ultis

import com.example.muzix.model.Artist
import com.example.muzix.model.Notification
import com.example.muzix.model.Playlist

class CustomComparator : Comparator<Notification> {
    override fun compare(o1: Notification?, o2: Notification?): Int {
        return o1?.date.toString().compareTo(o2?.date.toString())
    }
}