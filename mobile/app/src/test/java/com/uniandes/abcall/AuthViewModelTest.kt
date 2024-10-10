package com.uniandes.abcall

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.uniandes.abcall.data.exceptions.ApiRequestException
import com.uniandes.abcall.data.model.AuthResponse
import com.uniandes.abcall.data.model.UserCredentials
import com.uniandes.abcall.data.repository.AuthRepository
import com.uniandes.abcall.viewmodel.AuthViewModel
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class AuthViewModelTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val testDispatcher = newSingleThreadContext("Test thread")

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var application: Application

    private lateinit var authViewModel: AuthViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authViewModel = AuthViewModel(application, authRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.close()
    }

    @Test
    fun `login successful`() {
        val userCredentials = UserCredentials("testUser", "password")
        val mockResponse = AuthResponse("mockToken", "12345")

        runBlocking {
            Mockito.`when`(authRepository.login(userCredentials)).thenReturn(mockResponse)
        }

        authViewModel.login(userCredentials)

        val value = authViewModel.authResponse.getOrAwaitValue()
        assertNotNull(value)
        if (value != null) {
            assertEquals("mockToken", value.token)
        }
    }

    @Test
    fun `login failed`(){
        val userCredentials = UserCredentials("testUser", "password")

        runBlocking {
            Mockito.`when`(authRepository.login(userCredentials)).thenThrow(ApiRequestException("Login failed"))
        }

        authViewModel.login(userCredentials)

        val error = authViewModel.error.getOrAwaitValue()
        assertEquals("Error de autenticación: Login failed", error)
    }

    @Test
    fun `fetchAndStoreUserInfo successful`() {
        val token = "mockToken"

        runBlocking {
            // No necesitas hacer nada aquí ya que fetchAndStoreUserInfo no lanza ninguna excepción
            Mockito.`when`(authRepository.fetchAndStoreUserInfo(token)).thenReturn(Unit)
        }

        // Ejecutamos el método a probar
        authViewModel.fetchAndStoreUserInfo(token)

        // Verificamos que no se haya producido ningún error
        try {
            val errorValue = authViewModel.error.getOrAwaitValue(time = 1, timeUnit = TimeUnit.SECONDS)
            // Si el valor no es null, la prueba falla
            assertEquals(null, errorValue)
        } catch (e: TimeoutException) {
            // Si se lanza TimeoutException, significa que el valor de error nunca se actualizó, lo cual es esperado
            assert(true)
        }
    }

    @Test
    fun `fetchAndStoreUserInfo failed`() {
        val token = "mockToken"
        val exceptionMessage = "Failed to fetch user info"

        runBlocking {
            // Simulamos que el repositorio lanza una ApiRequestException
            Mockito.`when`(authRepository.fetchAndStoreUserInfo(token))
                .thenThrow(ApiRequestException(exceptionMessage))
        }

        // Llamamos al método del ViewModel
        authViewModel.fetchAndStoreUserInfo(token)

        // Verificamos que el error en el ViewModel se haya actualizado correctamente
        val errorValue = authViewModel.error.getOrAwaitValue()
        assertEquals(exceptionMessage, errorValue)
    }

}