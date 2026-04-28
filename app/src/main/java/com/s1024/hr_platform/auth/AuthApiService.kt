package com.s1024.hr_platform.auth

import retrofit2.http.Body
import retrofit2.http.POST


data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val success: Boolean, val message: String)

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse
}