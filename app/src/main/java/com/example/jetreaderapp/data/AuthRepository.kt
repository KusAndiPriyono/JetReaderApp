package com.example.jetreaderapp.data

import com.example.jetreaderapp.utils.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(
        firstName: String,
        lastName: String, email: String, password: String
    ): Flow<Resource<AuthResult>>

    suspend fun saveUserData(
        userId: String,
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Resource<Unit>>
}