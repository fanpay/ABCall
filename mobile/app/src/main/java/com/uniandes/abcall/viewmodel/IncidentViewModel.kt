package com.uniandes.abcall.viewmodel

import android.app.Application
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


    // LiveData para observar las incidencias desde Room
    val incidents: LiveData<List<Incident>> = incidentRepository.getAllIncidents(userId)

    init {
        // Sincronizar incidencias desde la API cuando se crea el ViewModel
        syncIncidents(userId)
    }

    // Sincronizar incidencias desde la API y almacenarlas en Room
    fun syncIncidents(userId: String) {
        viewModelScope.launch {
            incidentRepository.syncIncidents(userId)
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