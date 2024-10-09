package com.uniandes.abcall.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uniandes.abcall.R
import com.uniandes.abcall.data.repository.AuthRepository
import com.uniandes.abcall.data.repository.IncidentRepository
import com.uniandes.abcall.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var authRepository: AuthRepository
    private lateinit var incidentsRepository: IncidentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        authRepository = AuthRepository(application)
        incidentsRepository = IncidentRepository(application)

        // Recuperar el token pasado desde la actividad de login si es necesario
        val token = intent.getStringExtra("TOKEN")
        val userId = intent.getStringExtra("USER_ID")

        Log.e("HomeActivity", "Token: $token")
        Log.e("HomeActivity", "USER_ID: $userId")

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // Respond to navigation item 1 click
                    true
                }
                R.id.menu_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        if (userId != null) {
            // Cargar los datos del usuario desde la base de datos local (Room) usando el ID
            userViewModel.getUser(userId)
        }
    }

    private fun logout() {
        // Eliminar token o información de sesión almacenada
        clearSessionData()

        // Eliminar la información de la tabla users_table en Room
        CoroutineScope(Dispatchers.IO).launch {
            authRepository.clearUsersTable()
            incidentsRepository.clearIncidentsTable()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun clearSessionData() {
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}