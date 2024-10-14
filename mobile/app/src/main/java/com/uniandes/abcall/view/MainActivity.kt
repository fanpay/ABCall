package com.uniandes.abcall.view


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.uniandes.abcall.R
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
        authViewModel.authResponse.observe(this) { authResponse ->
            if (authResponse != null) {
                // Login exitoso, redirigir a HomeActivity

                Snackbar.make(findViewById(android.R.id.content), R.string.successfull_login, Snackbar.LENGTH_SHORT).show()

                // Consultar la información del usuario usando el token
                authViewModel.fetchAndStoreUserInfo(authResponse.token)

                // Redirigir a HomeActivity después de obtener los datos
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("TOKEN", authResponse.token)
                intent.putExtra("USER_ID", authResponse.id)

                startActivity(intent)
                finish() // Cerrar esta actividad
            }
        }

        // Observa posibles errores
        authViewModel.error.observe(this) { error ->
            if (error != null) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show()
            }
        }

        // Configurar el botón de login
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validar que los campos no estén vacíos antes de intentar el login
            if (username.isNotEmpty() && password.isNotEmpty()) {
                val userCredentials = UserCredentials(username, password)
                authViewModel.login(userCredentials)
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.validation_data_login, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}