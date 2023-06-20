package com.example.muzix.data.local

import androidx.room.*
import com.example.muzix.model.Notification

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification")
    fun getNotification() : List<Notification>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: Notification)

    @Query("DELETE FROM notification")
    fun deleteAll()

    @Delete
    fun deleteItem(item : Notification)
}