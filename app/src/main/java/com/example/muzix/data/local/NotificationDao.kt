package com.example.muzix.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.muzix.model.Notification

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification")
    fun getNotification() : List<Notification>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: Notification)
}