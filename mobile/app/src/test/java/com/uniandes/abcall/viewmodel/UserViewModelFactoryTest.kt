package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.uniandes.abcall.data.repository.AuthRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
class UserViewModelFactoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val testDispatcher = newSingleThreadContext("Test thread")

    private lateinit var application: Application
    private lateinit var userViewModelFactory: UserViewModelFactory
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = mock(Application::class.java)
        authRepository = mock(AuthRepository::class.java)
        userViewModelFactory = UserViewModelFactory(application, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.close()
    }

    @Test
    fun `create UserViewModel successfully`() {
        // Verificamos que UserViewModelFactory crea una instancia de UserViewModel
        val viewModel = userViewModelFactory.create(UserViewModel::class.java)
        assertNotNull(viewModel)
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
