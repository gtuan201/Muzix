package com.example.muzix.model

import android.os.Parcel
import android.os.Parcelable

data class Song(
    val id : String? = null,
    val name : String? = null,
    val duration : String? = null,
    val description : String? = null,
    val image : String? = null,
    val artist : String? = null,
    val mp3 : String? = null,
    val idPlaylist: String? = null,
    val listens : Long? = null,
    val genre : String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(duration)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeString(artist)
        parcel.writeString(mp3)
        parcel.writeString(idPlaylist)
        parcel.writeValue(listens)
        parcel.writeString(genre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}