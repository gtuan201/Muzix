package com.example.muzix.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.muzix.model.Notification

@Database(entities = [Notification::class], version = 2)
abstract class AppDatabase : RoomDatabase(){
    abstract fun getDao() : NotificationDao
    companion object {
        fun createDatabase(appContext: Context): AppDatabase {
            return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "database"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigrationFrom(1)
                .build()
        }
    }
}