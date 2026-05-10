package com.example.kwikq.repository

import android.content.Context
import com.example.kwikq.database.AppDatabase
import com.example.kwikq.database.entity.User
import com.example.kwikq.database.entity.UserProfile
import com.example.kwikq.network.AuthApiService
import com.example.kwikq.network.AuthModels

class UserRepository(private val context: Context, private val authService: AuthApiService) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val userProfileDao = database.userProfileDao()

    /**
     * Login user and cache in local database
     */
    suspend fun loginUser(email: String, password: String): Result<AuthModels.AuthResponse> {
        return try {
            val response = authService.login(AuthModels.LoginRequest(email, password))
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    // Cache user in local database
                    val user = User(
                        id = authResponse.id,
                        name = authResponse.name,
                        email = authResponse.email,
                        password = "", // Don't store password for security
                        role = authResponse.role
                    )
                    userDao.insert(user)
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception("Invalid auth response"))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Register user and cache in local database
     */
    suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Result<AuthModels.AuthResponse> {
        return try {
            val response = authService.register(
                AuthModels.RegisterRequest(name, email, password)
            )
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    // Cache user in local database
                    val user = User(
                        id = authResponse.id,
                        name = authResponse.name,
                        email = authResponse.email,
                        password = "", // Don't store password for security
                        role = authResponse.role
                    )
                    userDao.insert(user)
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception("Invalid auth response"))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get cached user by ID
     */
    suspend fun getCachedUser(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    /**
     * Get cached user by email
     */
    suspend fun getCachedUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    /**
     * Get cached user profile
     */
    suspend fun getCachedUserProfile(userId: Long): UserProfile? {
        return userProfileDao.getProfileByUserId(userId)
    }

    /**
     * Save user profile to local database
     */
    suspend fun saveUserProfile(userProfile: UserProfile) {
        userProfileDao.insert(userProfile)
    }

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.update(userProfile)
    }

    /**
     * Clear all user data (for logout)
     */
    suspend fun clearAllUserData() {
        userProfileDao.deleteAllProfiles()
        userDao.deleteAllUsers()
    }

    /**
     * Clear specific user data (for logout)
     */
    suspend fun clearUserData(userId: Long) {
        userProfileDao.deleteProfileByUserId(userId)
        userDao.deleteUserById(userId)
    }
}
