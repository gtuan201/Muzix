package com.example.muzix.model

import android.os.Parcel
import android.os.Parcelable

data class Playlist(
    val id : String? = null,
    val name : String? = null,
    val description : String? = null,
    val owner : String? = null,
    val dayCreate : String? = null,
    val thumbnail : String? = null,
    val tracks : List<Song>? = null,
    val duration : String? = null,
    val lover : Long? = null,
    val idCollection : String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Song),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(owner)
        parcel.writeString(dayCreate)
        parcel.writeString(thumbnail)
        parcel.writeTypedList(tracks)
        parcel.writeString(duration)
        parcel.writeValue(lover)
        parcel.writeString(idCollection)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(parcel: Parcel): Playlist {
            return Playlist(parcel)
        }

        override fun newArray(size: Int): Array<Playlist?> {
            return arrayOfNulls(size)
        }
    }
}
