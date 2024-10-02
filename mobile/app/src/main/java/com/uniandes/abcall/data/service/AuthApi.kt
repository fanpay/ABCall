package com.uniandes.abcall.data.service

import com.uniandes.abcall.data.model.AuthResponse
import com.uniandes.abcall.data.model.UserCredentials
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("users/auth")
    suspend fun login(@Body credentials: UserCredentials): AuthResponse

}