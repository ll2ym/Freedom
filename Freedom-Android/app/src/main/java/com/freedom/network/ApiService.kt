package com.freedom.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserResponse>

    @PUT("auth/me")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body request: ProfileUpdateRequest
    ): Response<UserResponse>
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    @SerializedName("invite_code")
    val inviteCode: String
)

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String = "bearer"
)

data class UserResponse(
    val username: String,
    @SerializedName("display_name")
    val displayName: String?,
    val status: String?
)

data class ProfileUpdateRequest(
    @SerializedName("display_name")
    val displayName: String,
    val status: String
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