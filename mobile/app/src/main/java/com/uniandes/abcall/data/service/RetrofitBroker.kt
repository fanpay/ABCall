package com.uniandes.abcall.data.service

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
                    onComplete(response.body() ?: emptyList())
                } else {
                    onError(Exception("getIncidents -> Error en la solicitud a la API: ${response.code()} - ${response.raw()} - ${response.body()}"))
                }
            } catch (e: Throwable) {
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
