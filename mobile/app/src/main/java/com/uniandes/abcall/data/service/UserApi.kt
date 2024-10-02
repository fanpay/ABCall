package com.uniandes.abcall.data.service

import com.uniandes.abcall.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {

    @GET("/users/me")
    suspend fun getUserInfo(@Header("Authorization") token: String): User

    /*@GET("user")
    suspend fun getUser(): Response<User>*/

}