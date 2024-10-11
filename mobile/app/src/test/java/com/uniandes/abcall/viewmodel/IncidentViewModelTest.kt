package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.data.repository.IncidentRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import java.sql.Timestamp

@ExperimentalCoroutinesApi
class IncidentViewModelTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val testDispatcher = newSingleThreadContext("Test thread")

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var incidentRepository: IncidentRepository

    private lateinit var incidentViewModel: IncidentViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val incidentsLiveData = MutableLiveData<List<Incident>>()

        whenever(incidentRepository.getAllIncidents("testUserId"))
            .thenReturn(incidentsLiveData)

        incidentViewModel = IncidentViewModel(application, "testUserId", incidentRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.close()
    }

    @Test
    fun `syncIncidents updates LiveData`() = runTest {
        val newIncident = Incident(
            id = 1,
            userId = "testUserId",
            subject = "Test Subject",
            description = "Test Description",
            originType = "Email",
            status = "OPEN",
            solution = null,
            creationDate = Timestamp(System.currentTimeMillis()),
            updateDate = Timestamp(System.currentTimeMillis()),
            solutionAgentId = null,
            solutionDate = null
        )

        // Configurar el LiveData simulado con una lista vacía
        val incidentsLiveData = MutableLiveData<List<Incident>>()
        incidentsLiveData.value = emptyList()

        // Configurar el mock para devolver el LiveData simulado
        whenever(incidentRepository.getAllIncidents("testUserId")).thenReturn(incidentsLiveData)

        // Simular el comportamiento de syncIncidents actualizando el LiveData
        doAnswer {
            incidentsLiveData.postValue(listOf(newIncident)) // Publicar el nuevo incidente en el LiveData
            null
        }.whenever(incidentRepository).syncIncidents("testUserId")

        // Inicializar el ViewModel
        incidentViewModel = IncidentViewModel(application, "testUserId", incidentRepository)

        // Capturar los cambios en el LiveData
        var capturedIncidents: List<Incident>? = null
        val observer = Observer<List<Incident>> { capturedIncidents = it }
        incidentViewModel.incidents.observeForever(observer)

        try {
            // Llamar al método de sincronización
            incidentViewModel.syncIncidents("testUserId")

            // Adelantar las tareas pendientes para completar las operaciones asíncronas
            advanceUntilIdle()

            // Verificar que el LiveData se haya actualizado
            assertNotNull("El valor del LiveData debería no ser null", capturedIncidents)
            assertEquals("La lista debería tener un incidente", 1, capturedIncidents?.size)
            assertEquals("El asunto debería ser 'Test Subject'", "Test Subject", capturedIncidents?.get(0)?.subject)
            assertEquals("La descripción debería ser 'Test Description'", "Test Description", capturedIncidents?.get(0)?.description)
        } finally {
            incidentViewModel.incidents.removeObserver(observer)
        }
    }

    @Test
    fun `addIncident updates LiveData`() = runTest{
        val newIncident = Incident(
            id = 2,
            userId = "testUserId",
            subject = "New Incident",
            description = "New Description",
            originType = "Phone",
            status = "OPEN",
            solution = null,
            creationDate = Timestamp(System.currentTimeMillis()),
            updateDate = Timestamp(System.currentTimeMillis()),
            solutionAgentId = null,
            solutionDate = null
        )

        val updatedIncidents = MutableLiveData<List<Incident>>()
        updatedIncidents.value = emptyList()
        whenever(incidentRepository.getAllIncidents("testUserId"))
            .thenReturn(updatedIncidents)

        // Simulamos el comportamiento de createIncident invocando manualmente el callback onComplete
        doAnswer {
            updatedIncidents.postValue(listOf(newIncident))
            val onComplete = it.getArgument<(Incident) -> Unit>(1)
            onComplete(newIncident) // Invocamos onComplete con el nuevo incidente
            null
        }.whenever(incidentRepository).createIncident(
            eq(newIncident),
            any(),
            any()
        )

        incidentViewModel = IncidentViewModel(application, "testUserId", incidentRepository)


        // Observador para capturar los valores emitidos por el LiveData
        var capturedIncidents: List<Incident>? = null
        val observer = Observer<List<Incident>> { capturedIncidents = it }
        incidentViewModel.incidents.observeForever(observer)

        try {
            // Llamamos al método para agregar el incidente
            incidentViewModel.addIncident(newIncident)

            // Aseguramos que las tareas asíncronas se completen
            advanceUntilIdle()

            // Verificamos que el LiveData se haya actualizado
            assertNotNull("El valor del LiveData debería no ser null", capturedIncidents)
            assertEquals("La lista debería tener un incidente", 1, capturedIncidents?.size)
            assertEquals("El ID del incidente debería ser '2'", 2, capturedIncidents?.get(0)?.id)
            assertEquals("El asunto debería ser 'New Incident'", "New Incident", capturedIncidents?.get(0)?.subject)
            assertEquals("La descripción debería ser 'New Description'", "New Description", capturedIncidents?.get(0)?.description)
        } finally {
            incidentViewModel.incidents.removeObserver(observer)
        }

    }

    @Test
    fun `incidents LiveData is initialized correctly`() {
        // Observador para verificar el estado inicial de los incidentes
        val incidentsLiveData = MutableLiveData<List<Incident>>()
        incidentsLiveData.value = emptyList()

        whenever(incidentRepository.getAllIncidents("testUserId"))
            .thenReturn(incidentsLiveData)

        // Reinicializamos el ViewModel para que use el mock configurado
        incidentViewModel = IncidentViewModel(application, "testUserId", incidentRepository)

        // Observador para verificar el estado inicial de los incidentes
        val observer = Observer<List<Incident>> {}
        try {
            incidentViewModel.incidents.observeForever(observer)
            // Verificamos que el LiveData no sea null
            assertNotNull(incidentViewModel.incidents.value)
        } finally {
            incidentViewModel.incidents.removeObserver(observer)
        }
    }
}
