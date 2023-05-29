package com.example.muzix.model

data class History(
    val id : String? = null,
    val idPlaylist: String? = null,
    val uid : String? = null
):Comparable<History> {
    override fun compareTo(other: History): Int {
        return this.id.toString().compareTo(other.id.toString())
    }
}
