package com.example.kwikq.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kwikq.database.dao.UserDao
import com.example.kwikq.database.dao.UserProfileDao
import com.example.kwikq.database.entity.User
import com.example.kwikq.database.entity.UserProfile

@Database(
    entities = [User::class, UserProfile::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kwikq_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
