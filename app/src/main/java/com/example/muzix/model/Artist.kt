package com.example.muzix.model

data class Artist(
    val id : String? = null,
    val name : String? = null,
    val image : String? = null,
    val birthday : String? = null,
    val nation : String? = null,
    val musicGenre : String? = null,
    val listPlaylist: List<Playlist>? = null,
    val listSong : List<Song>? = null,
    val description : String? = null,
    val lover : Long? = null
) : Comparable<Artist> {
    override fun compareTo(other: Artist): Int {
        return lover?.compareTo(other.lover!!) ?: 0
    }
}
