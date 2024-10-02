package com.uniandes.abcall.data.service

import com.uniandes.abcall.data.model.Incident
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IncidentApi {

    @GET("incidents")
    suspend fun getAllIncidents(): Response<List<Incident>>

    @POST("incidents")
    suspend fun createIncident(@Body incident: Incident) : Response<Incident>

}