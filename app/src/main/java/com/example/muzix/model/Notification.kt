package com.example.muzix.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null,
    val title : String? = null,
    val body : String? = null,
    val image : String? = null,
    val id_playlist : String? = null
)
