package com.uniandes.abcall.view

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.uniandes.abcall.R
import com.uniandes.abcall.viewmodel.UserViewModel

class HomeActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Vincular las vistas
        val userNameTextView = findViewById<TextView>(R.id.user_name)

        // Recuperar el token pasado desde la actividad de login si es necesario
        val token = intent.getStringExtra("TOKEN")
        val userId = intent.getStringExtra("USER_ID")

        Log.e("HomeActivity", "Token: $token")
        Log.e("HomeActivity", "USER_ID: $userId")

        if (userId != null) {
            // Observa los datos del usuario y los muestra en la interfaz
            userViewModel.user.observe(this) { user ->
                if (user != null) {
                    userNameTextView.text = "Bienvenido, ${user.fullName}"
                } else {
                    userNameTextView.text = "Usuario no encontrado"
                }
            }

            // Cargar los datos del usuario desde la base de datos local (Room) usando el ID
            userViewModel.getUser(userId)
        } else {
            userNameTextView.text = "ID de usuario no proporcionado"
        }
    }
}