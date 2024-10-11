package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class AuthViewModelFactoryTest {

    private lateinit var application: Application
    private lateinit var authViewModelFactory: AuthViewModelFactory

    @Before
    fun setUp() {
        // Creamos un mock de la aplicación para pasar al ViewModelFactory
        application = mock(Application::class.java)
        authViewModelFactory = AuthViewModelFactory(application)
    }

    @Test
    fun `create AuthViewModel successfully`() {
        // Verificamos que AuthViewModelFactory crea una instancia de AuthViewModel
        val viewModel = authViewModelFactory.create(AuthViewModel::class.java)
        assertNotNull(viewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with unknown ViewModel class throws exception`() {
        // Intentamos crear una instancia de una clase ViewModel desconocida y verificamos que lanza IllegalArgumentException
        authViewModelFactory.create(UnknownViewModel::class.java)
        fail("Se esperaba una IllegalArgumentException al pasar una clase de ViewModel desconocida")
    }

    // Clase ficticia para simular un ViewModel desconocido
    class UnknownViewModel : ViewModel()
}