package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class UserViewModelFactoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application
    private lateinit var userViewModelFactory: UserViewModelFactory

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = mock(Application::class.java)
        userViewModelFactory = UserViewModelFactory(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create UserViewModel successfully`() {
        // Verificamos que UserViewModelFactory crea una instancia de UserViewModel
        val viewModel = userViewModelFactory.create(UserViewModel::class.java)
        assertTrue(viewModel is UserViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with unknown ViewModel class throws exception`() {
        // Intentamos crear una instancia de una clase ViewModel desconocida y verificamos que lanza IllegalArgumentException
        userViewModelFactory.create(UnknownViewModel::class.java)
        fail("Se esperaba una IllegalArgumentException al pasar una clase de ViewModel desconocida")
    }

    // Clase ficticia para simular un ViewModel desconocido
    class UnknownViewModel : ViewModel()
}
