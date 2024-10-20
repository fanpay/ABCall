package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.uniandes.abcall.data.model.User
import com.uniandes.abcall.data.repository.AuthRepository
import com.uniandes.abcall.getOrAwaitValue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
class UserViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val testDispatcher = newSingleThreadContext("Test thread")

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var userViewModel: UserViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)

        // Inyectamos el mock del AuthRepository al UserViewModel
        authRepository = mock(AuthRepository::class.java)
        userViewModel = UserViewModel(application, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.close()
    }

    @Test
    fun `getUser updates LiveData with valid user`(){
        val userId = "123"
        val mockUser = User(
            id = userId,
            username = "johndoe",
            email = "john.doe@example.com",
            fullName = "John Doe",
            dni = "12345678",
            phoneNumber = "1234567890",
            role = "Admin",
            token = "token_abc123"
        )

        // Configuramos el comportamiento del mock para devolver el usuario simulado
        runBlocking {
            Mockito.`when`(authRepository.getStoredUser(userId))
                .thenReturn(mockUser)
        }

        // Llamamos al método getUser para actualizar el LiveData
        userViewModel.getUser(userId)

        // Utilizamos getOrAwaitValue para esperar el valor del LiveData
        val capturedUser = userViewModel.user.getOrAwaitValue()

        // Verificamos que el LiveData se haya actualizado con el usuario simulado
        Assert.assertNotNull("El usuario no debería ser null", capturedUser)
        Assert.assertEquals("El ID del usuario debería ser 123", "123", capturedUser!!.id)
        Assert.assertEquals("El username del usuario debería ser johndoe", "johndoe", capturedUser.username)
        Assert.assertEquals("El email del usuario debería ser john.doe@example.com", "john.doe@example.com", capturedUser.email)
        Assert.assertEquals("El nombre completo debería ser John Doe", "John Doe", capturedUser.fullName)
        Assert.assertEquals("El DNI debería ser 12345678", "12345678", capturedUser.dni)
        Assert.assertEquals("El número de teléfono debería ser 1234567890", "1234567890", capturedUser.phoneNumber)
        Assert.assertEquals("El rol debería ser Admin", "Admin", capturedUser.role)
        Assert.assertEquals("El token debería ser token_abc123", "token_abc123", capturedUser.token)
    }

    @Test
    fun `getUser updates LiveData with null when user is not found`() = runTest {
        val userId = "123"

        // Configuramos el comportamiento del mock para devolver null
        Mockito.`when`(authRepository.getStoredUser(userId)).thenReturn(null)

        // Observador para capturar los cambios en el LiveData
        var capturedUser: User? = null
        val observer = Observer<User?> { user ->
            capturedUser = user
        }
        userViewModel.user.observeForever(observer)

        try {
            // Llamamos al método getUser para actualizar el LiveData
            userViewModel.getUser(userId)

            // Sincronizamos las coroutines para completar las tareas pendientes
            advanceUntilIdle()

            // Verificamos que el LiveData se haya actualizado con null
            Assert.assertNull("El usuario debería ser null", capturedUser)
        } finally {
            userViewModel.user.removeObserver(observer)
        }
    }
}
