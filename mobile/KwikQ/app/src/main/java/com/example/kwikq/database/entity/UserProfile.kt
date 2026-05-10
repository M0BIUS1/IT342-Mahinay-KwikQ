package com.example.kwikq.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profiles",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserProfile(
    @PrimaryKey
    val id: Long,
    val userId: Long,
    val phoneNumber: String? = null,
    val address: String? = null,
    val bio: String? = null,
    val borrowingLimit: Int = 5,
    val activeBorrows: Int = 0,
    val totalFines: Double = 0.0,
    val isBlocked: Boolean = false,
    val profilePictureUrl: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
