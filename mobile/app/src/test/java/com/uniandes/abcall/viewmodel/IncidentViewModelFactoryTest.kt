package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.data.repository.IncidentRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.whenever
import java.sql.Timestamp

@ExperimentalCoroutinesApi
class IncidentViewModelFactoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val testDispatcher = newSingleThreadContext("Test thread")

    private lateinit var application: Application
    private lateinit var incidentRepository: IncidentRepository
    private lateinit var incidentViewModelFactory: IncidentViewModelFactory
    private val userId = "testUserId"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = mock(Application::class.java)

        // Mockeamos el IncidentRepository
        incidentRepository = mock(IncidentRepository::class.java)

        // Configuramos el IncidentRepository simulado
        val mockIncidents = MutableLiveData<List<Incident>>(emptyList())
        whenever(incidentRepository.getAllIncidents(userId)).thenReturn(mockIncidents)

        // Creamos el IncidentViewModelFactory con el repositorio simulado
        incidentViewModelFactory = object : IncidentViewModelFactory(application, userId) {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(IncidentViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return IncidentViewModel(application, userId, incidentRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.close()
    }

    @Test
    fun `create IncidentViewModel successfully`() {
        val mockIncident = Incident(
            id = 1,
            userId = userId,
            subject = "Mock Subject",
            description = "Mock Description",
            originType = "Email",
            status = "OPEN",
            solution = null,
            creationDate = Timestamp(System.currentTimeMillis()),
            updateDate = Timestamp(System.currentTimeMillis()),
            solutionAgentId = null,
            solutionDate = null
        )
        val mockIncidents = MutableLiveData(listOf(mockIncident))
        whenever(incidentRepository.getAllIncidents(userId)).thenReturn(mockIncidents)


        // Verificamos que IncidentViewModelFactory crea una instancia de IncidentViewModel
        val viewModel = incidentViewModelFactory.create(IncidentViewModel::class.java)
        assertNotNull(viewModel)
        assertSame(application, viewModel.getApplication())
        assertEquals(userId, viewModel.incidents.value?.firstOrNull()?.userId)
        assertEquals("Mock Subject", viewModel.incidents.value?.firstOrNull()?.subject)
    }

    @Test
    fun `create with unknown ViewModel class throws exception`() {
        try {
            incidentViewModelFactory.create(UnknownViewModel::class.java)
            fail("Se esperaba una IllegalArgumentException al pasar una clase de ViewModel desconocida")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Unknown ViewModel class") == true)
        }
    }

    // Clase ficticia para simular un ViewModel desconocido
    class UnknownViewModel : ViewModel()
}


