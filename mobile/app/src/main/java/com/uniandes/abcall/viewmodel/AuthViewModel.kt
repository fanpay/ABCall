package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uniandes.abcall.R
import com.uniandes.abcall.data.exceptions.ApiRequestException
import com.uniandes.abcall.data.model.AuthResponse
import com.uniandes.abcall.data.model.UserCredentials
import com.uniandes.abcall.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
        application: Application,
        private var authRepository: AuthRepository = AuthRepository(application)
    ) : AndroidViewModel(application) {


    private val _authResponse = MutableLiveData<AuthResponse?>()
    val authResponse: LiveData<AuthResponse?> = _authResponse

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(userCredentials: UserCredentials) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(userCredentials)
                _authResponse.postValue(response)
            } catch (e: Exception) {
                val errorMessage = getApplication<Application>().getString(R.string.error_login) + ": " + e.message
                _error.postValue(errorMessage)
            }
        }
    }

    // Método para obtener la información del usuario y almacenarla en Room
    fun fetchAndStoreUserInfo(token: String) {
        viewModelScope.launch {
            try {
                authRepository.fetchAndStoreUserInfo(token)
            } catch (e: ApiRequestException) {
                _error.postValue(e.message)
            }
        }
    }
}