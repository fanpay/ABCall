package com.uniandes.abcall.data.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://10.0.2.2:9876/"
//private const val BASE_URL = "http://192.168.1.102:9876/"
//private const val BASE_URL = "https://heroku-vinyls-g8-d9e277b35953.herokuapp.com/"

object ApiClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val incidents : IncidentApi by lazy {
        retrofit.create(IncidentApi::class.java)
    }

    val user: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val auth: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}