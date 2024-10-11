package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uniandes.abcall.data.model.User
import com.uniandes.abcall.data.repository.AuthRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    // Obtener los datos del usuario desde Room
    fun getUser(id: String) {
        viewModelScope.launch {
            val user = authRepository.getStoredUser(id)
            _user.postValue(user)
        }
    }
}