package com.uniandes.abcall.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.uniandes.abcall.R


import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.uniandes.abcall.data.model.UserCredentials
import com.uniandes.abcall.viewmodel.AuthViewModel
import com.uniandes.abcall.viewmodel.AuthViewModelFactory

class MainActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vincular las vistas
        val usernameEditText = findViewById<EditText>(R.id.fieldUser)
        val passwordEditText = findViewById<EditText>(R.id.passField)
        val loginButton = findViewById<Button>(R.id.btn_login)

        // Observa los resultados del login
        authViewModel.authResponse.observe(this, Observer { authResponse ->
            if (authResponse != null) {
                // Login exitoso, redirigir a HomeActivity
                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()

                // Consultar la información del usuario usando el token
                authViewModel.fetchAndStoreUserInfo(authResponse.token)

                // Redirigir a HomeActivity después de obtener los datos
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("TOKEN", authResponse.token)
                intent.putExtra("USER_ID", authResponse.id)

                Log.e("MainActivity", "Token: ${authResponse.token}")
                Log.e("MainActivity", "USER_ID: ${authResponse.id}")
                startActivity(intent)
                finish() // Cerrar esta actividad
            }
        })

        // Observa posibles errores
        authViewModel.error.observe(this, Observer { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        })

        // Configurar el botón de login
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validar que los campos no estén vacíos antes de intentar el login
            if (username.isNotEmpty() && password.isNotEmpty()) {
                val userCredentials = UserCredentials(username, password)
                authViewModel.login(userCredentials)
            } else {
                Toast.makeText(this, "Por favor, ingresa tu usuario y contraseña.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}