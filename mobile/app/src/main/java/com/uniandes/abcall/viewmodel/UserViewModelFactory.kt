package com.uniandes.abcall.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uniandes.abcall.data.repository.AuthRepository

class UserViewModelFactory(
    private val application: Application,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(application, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
