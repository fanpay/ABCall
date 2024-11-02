package com.uniandes.abcall.data.service

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp

//private const val USERS_URL = "https://ms-users-719013485218.us-central1.run.app/"
//private const val INCIDENTS_URL = "https://ms-incidents-719013485218.us-central1.run.app/"

private const val USERS_URL = "https://misw-2024-api-gateway-pf-96b62bvm.uc.gateway.dev/"
private const val INCIDENTS_URL = "https://misw-2024-api-gateway-pf-96b62bvm.uc.gateway.dev/"
private const val CHATBOT_URL = "https://misw-2024-api-gateway-pf-96b62bvm.uc.gateway.dev/"

//private const val BASE_URL = "https://heroku-vinyls-g8-d9e277b35953.herokuapp.com/"

object ApiClient {
    // Crear una instancia de Gson con el adaptador personalizado
    val gson = GsonBuilder()
        .registerTypeAdapter(Timestamp::class.java, TimestampAdapter())
        .create()

    private val retrofitUser: Retrofit = Retrofit.Builder()
        .baseUrl(USERS_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitIncident: Retrofit = Retrofit.Builder()
        .baseUrl(INCIDENTS_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val incidents : IncidentApi by lazy {
        retrofitIncident.create(IncidentApi::class.java)
    }

    val user: UserApi by lazy {
        retrofitUser.create(UserApi::class.java)
    }

    val auth: AuthApi by lazy {
        retrofitUser.create(AuthApi::class.java)
    }
}