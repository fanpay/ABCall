package com.uniandes.abcall.data.repository

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
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

    suspend fun getAllIncidents(): List<Incident> {
        return withContext(Dispatchers.IO) {

            try {
                val cached = incidentsDao.getIncidents()

                if (!isNetworkAvailable() && cached.isNotEmpty()) {
                    Log.v("IncidentRepository", "No Internet. Retrieving cached incidents. ${cached.size} found.")
                    return@withContext cached
                }

                if (!isNetworkAvailable() && cached.isEmpty()) {
                    Log.v("IncidentRepository", "No Internet. No cached data was found. Retrieving empty list.")
                    return@withContext cached
                }

                if (cached.isNotEmpty()) {
                    Log.v("IncidentRepository", "Retrieving cached incidents. ${cached.size} found.")
                    return@withContext cached
                }

                var incidents: List<Incident> = emptyList()

                RetrofitBroker.getIncidents(
                    onComplete = { response ->
                        incidents = response
                        CoroutineScope(Dispatchers.IO).launch {
                            insertIncidentsIntoDatabase(incidents)
                        }
                    },
                    onError = { error ->
                        throw ApiRequestException(
                            application.resources.getString(
                                R.string.error_retrieve_incidents
                            ), error
                        )
                    }
                )

                return@withContext incidents
            } catch (e: Throwable) {
                Log.e("IncidentRepository", "Error retrieving incidents from API: ${e.message}")
                throw ApiRequestException(
                    application.resources.getString(R.string.error_retrieve_incidents),
                    e
                )
            }
        }
    }

    suspend fun createIncident(incident: Incident,
                            onComplete: (resp: Incident) -> Unit,
                            onError: (error: Throwable) -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitBroker.createIncident(
                    incident,
                    onComplete = { response ->
                        CoroutineScope(Dispatchers.IO).launch {
                            insertIncidentIntoDatabase(response)
                        }
                        onComplete(response)
                    },
                    onError = { error ->
                        onError(error)
                        throw ApiRequestException(
                            application.resources.getString(
                                R.string.error_retrieve_incidents
                            ), error
                        )
                    }
                )

                return@withContext response
            } catch (e: Throwable) {
                Log.e("IncidentRepository", "Error creating incident from API: ${e.message}")
                throw ApiRequestException(
                    application.resources.getString(R.string.error_save_album),
                    e
                )
            }
        }
    }

    private suspend fun isNetworkAvailable(): Boolean = withContext(Dispatchers.IO) {
        val cm = application.baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)
        return@withContext capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private suspend fun insertIncidentsIntoDatabase(incidents: List<Incident>) {
        incidentsDao.insertAll(incidents)
        Log.v("IncidentRepository", "Inserted ${incidents.size} incidents into the local database.")
    }

    private suspend fun insertIncidentIntoDatabase(incident: Incident) {
        incidentsDao.insert(incident)
        Log.v("IncidentRepository", "Inserted 1 incident into the local database. ID ${incident.incidentId}")
    }
}