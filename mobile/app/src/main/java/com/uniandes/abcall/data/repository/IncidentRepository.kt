package com.uniandes.abcall.data.repository

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import com.uniandes.abcall.R
import com.uniandes.abcall.data.database.IncidentsDao
import com.uniandes.abcall.data.database.ABCallRoomDatabase
import com.uniandes.abcall.data.exceptions.ApiRequestException
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.data.service.RetrofitBroker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IncidentRepository(val application: Application) {
    private val incidentsDao: IncidentsDao by lazy {
        ABCallRoomDatabase.getDatabase(application).incidentsDao()
    }

    // Retornar incidencias usando LiveData desde la base de datos
    fun getAllIncidents(userId:String): LiveData<List<Incident>> {
        return incidentsDao.getAllIncidents(userId)
    }

    // Sincroniza los incidentes desde la API y los guarda en Room
    suspend fun syncIncidents(userId:String) {
        if (isNetworkAvailable()) {
            try {
                RetrofitBroker.getIncidents(
                    userId,
                    onComplete = { response ->
                        // Guardar los incidentes en Room
                        Log.e("IncidentRepository", "Respuesta de incident: $response")
                        CoroutineScope(Dispatchers.IO).launch {
                            insertIncidentsIntoDatabase(response)
                        }
                    },
                    onError = { error ->
                        Log.e("IncidentRepository", "Error retrieving incidents from API: ${error.message}")
                        throw ApiRequestException(
                            application.resources.getString(R.string.error_retrieve_incidents),
                            error
                        )
                    }
                )
            } catch (e: Throwable) {
                Log.e("IncidentRepository", "Error retrieving incidents: ${e.message}")
                throw ApiRequestException(
                    application.resources.getString(R.string.error_retrieve_incidents),
                    e
                )
            }
        } else {
            Log.v("IncidentRepository", "No Internet. Using cached data.")
        }
    }

    // Crea una nueva incidencia llamando a la API y almacenándola en Room
    suspend fun createIncident(incident: Incident,
                               onComplete: (resp: Incident) -> Unit,
                               onError: (error: Throwable) -> Unit) {
        if (isNetworkAvailable()) {
            try {
                RetrofitBroker.createIncident(
                    incident,
                    onComplete = { response ->
                        // Almacenar en la base de datos local (Room)
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.e("IncidentRepository", "Respuesta de incident: $response")
                            insertIncidentIntoDatabase(response)
                        }
                        onComplete(response)
                    },
                    onError = { error ->
                        Log.e("IncidentRepository", "Error creating incident: ${error.message}")
                        onError(error)
                        throw ApiRequestException(
                            application.resources.getString(R.string.error_create_incident),
                            error
                        )
                    }
                )
            } catch (e: Throwable) {
                Log.e("IncidentRepository", "Error creating incident: ${e.message}")
                throw ApiRequestException(
                    application.resources.getString(R.string.error_create_incident),
                    e
                )
            }
        } else {
            // Manejo del caso sin internet si lo necesitas
            onError(Exception("No internet connection"))
        }
    }

    // Verificar si hay conexión a internet
    private fun isNetworkAvailable(): Boolean {
        val cm = application.baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    // Insertar incidentes en la base de datos Room
    private suspend fun insertIncidentsIntoDatabase(incidents: List<Incident>) {
        incidentsDao.insertAll(incidents)
        Log.v("IncidentRepository", "Inserted ${incidents.size} incidents into the local database.")
    }

    private suspend fun insertIncidentIntoDatabase(incident: Incident) {
        incidentsDao.insert(incident)
        Log.v("IncidentRepository", "Inserted 1 incident into the local database. ID ${incident.id}")
    }

    suspend fun clearIncidentsTable() {
        withContext(Dispatchers.IO) {
            incidentsDao.deleteAll()
        }
    }
}
