package com.uniandes.abcall.data.service

import android.util.Log
import com.uniandes.abcall.data.model.AuthResponse
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.data.model.User
import com.uniandes.abcall.data.model.UserCredentials

class RetrofitBroker {

    companion object {

        // Auth
        suspend fun login(credentials: UserCredentials): AuthResponse {
            return ApiClient.auth.login(credentials)
        }

        // User
        suspend fun getUserInfo(token: String): User {
            return ApiClient.user.getUserInfo("Bearer $token")
        }

        // Incidents
        suspend fun getIncidents(
            onComplete: (resp: List<Incident>) -> Unit,
            onError: (error: Throwable) -> Unit
        ) {
            try {
                val response = ApiClient.incidents.getAllIncidents()
                if (response.isSuccessful) {
                    Log.e("RetrofitBroker", "Incidentes obtenidos de la API: ${response.body()}")
                    onComplete(response.body() ?: emptyList())
                } else {
                    Log.e("RetrofitBroker", "Error en la solicitud a la API: ${response.code()} - ${response.message()}")
                    onError(Exception("getIncidents -> Error en la solicitud a la API: ${response.code()} - ${response.raw()} - ${response.body()}"))
                }
            } catch (e: Throwable) {
                Log.e("RetrofitBroker", "Excepción al obtener incidentes: ${e.message}")
                onError(e)
            }
        }

        suspend fun createIncident(incident: Incident,
                                onComplete: (resp: Incident) -> Unit,
                                onError: (error: Throwable) -> Unit) {
            try {
                val response = ApiClient.incidents.createIncident(incident)
                if (response.isSuccessful) {
                    response.body()?.let { onComplete(it) }
                } else {
                    onError(Exception("createIncident -> Error en la solicitud a la API: ${response.code()} - ${response.raw()} - ${response.body()}"))
                }
            } catch (e: Throwable) {
                onError(e)
            }
        }
    }
}