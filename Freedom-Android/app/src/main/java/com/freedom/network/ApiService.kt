package com.freedom.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserResponse>

    @GET("messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Query("with") username: String
    ): Response<List<MessageResponse>>

    @POST("messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body message: SendMessageRequest
    ): Response<MessageResponse>
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val inviteCode: String
)

data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "bearer"
)

data class UserResponse(
    val username: String,
    val displayName: String,
    val avatarUrl: String?
)

data class MessageResponse(
    val id: String,
    val text: String,
    val sender: String,
    val timestamp: Long
)

data class SendMessageRequest(
    val to: String,
    val text: String,
    val encrypted: Boolean = true
)