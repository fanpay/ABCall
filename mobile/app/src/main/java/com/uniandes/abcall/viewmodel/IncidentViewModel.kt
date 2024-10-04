package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.data.repository.IncidentRepository
import kotlinx.coroutines.launch

class IncidentViewModel(application: Application) : AndroidViewModel(application) {
    private val incidentRepository = IncidentRepository(application)

    // LiveData para observar las incidencias desde Room
    val incidents: LiveData<List<Incident>> = incidentRepository.getAllIncidents()

    init {
        // Sincronizar incidencias desde la API cuando se crea el ViewModel
        syncIncidents()
    }

    // Sincronizar incidencias desde la API y almacenarlas en Room
    fun syncIncidents() {
        viewModelScope.launch {
            incidentRepository.syncIncidents()
        }
    }

    // Método para agregar una nueva incidencia si es necesario
    fun addIncident(incident: Incident) {
        viewModelScope.launch {
            incidentRepository.createIncident(incident,
                onComplete = { /* Manejo de éxito si es necesario */ },
                onError = { /* Manejo de errores */ })
        }
    }
}