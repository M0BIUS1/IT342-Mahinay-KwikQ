package com.example.kwikq.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.kwikq.database.entity.UserProfile

@Dao
interface UserProfileDao {
    @Insert
    suspend fun insert(userProfile: UserProfile): Long

    @Update
    suspend fun update(userProfile: UserProfile)

    @Delete
    suspend fun delete(userProfile: UserProfile)

    @Query("SELECT * FROM user_profiles WHERE id = :profileId")
    suspend fun getProfileById(profileId: Long): UserProfile?

    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getProfileByUserId(userId: Long): UserProfile?

    @Query("DELETE FROM user_profiles WHERE userId = :userId")
    suspend fun deleteProfileByUserId(userId: Long)

    @Query("DELETE FROM user_profiles")
    suspend fun deleteAllProfiles()
}
