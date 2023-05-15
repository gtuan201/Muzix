package com.example.muzix.model

import android.os.Parcel
import android.os.Parcelable


data class Artist(
    val id : String? = null,
    val name : String? = null,
    val image : String? = null,
    val background : String? = null,
    val birthday : String? = null,
    val nation : String? = null,
    val musicGenre : String? = null,
    val listPlaylist: List<Playlist>? = null,
    val listSong : List<Song>? = null,
    val description : String? = null,
    val lover : Long? = null
) : Comparable<Artist>, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Playlist),
        parcel.createTypedArrayList(Song),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun compareTo(other: Artist): Int {
        return lover?.compareTo(other.lover!!) ?: 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(background)
        parcel.writeString(birthday)
        parcel.writeString(nation)
        parcel.writeString(musicGenre)
        parcel.writeTypedList(listPlaylist)
        parcel.writeTypedList(listSong)
        parcel.writeString(description)
        parcel.writeValue(lover)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Artist> {
        override fun createFromParcel(parcel: Parcel): Artist {
            return Artist(parcel)
        }

        override fun newArray(size: Int): Array<Artist?> {
            return arrayOfNulls(size)
        }
    }
}
