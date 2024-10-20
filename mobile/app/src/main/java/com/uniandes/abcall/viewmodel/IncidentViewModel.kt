package com.uniandes.abcall.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.data.repository.IncidentRepository
import kotlinx.coroutines.launch

class IncidentViewModel(application: Application,
                        userId: String,
                        private var incidentRepository:IncidentRepository = IncidentRepository(application)
    ) : AndroidViewModel(application) {


    val incidents: LiveData<List<Incident>> = incidentRepository.getAllIncidents(userId)


    init {
        // Sincronizar incidencias desde la API cuando se crea el ViewModel
        syncIncidents(userId)
    }

    // Sincronizar incidencias desde la API y almacenarlas en Room
    fun syncIncidents(userId: String) {
        viewModelScope.launch {
            try {
                // Sincroniza los incidentes desde el repositorio
                incidentRepository.syncIncidents(userId)
            } catch (e: Exception) {
                Log.e("IncidentViewModel", "Error sincronizando los incidentes: ${e.message}")
            }
        }
    }

    // Método para agregar una nueva incidencia si es necesario
    fun addIncident(incident: Incident) {
        viewModelScope.launch {
            incidentRepository.createIncident(
                incident,
                onComplete = {
                    incidentRepository.getAllIncidents(incident.userId)
                },
                onError = { /* Manejo de errores */ })
        }
    }
}