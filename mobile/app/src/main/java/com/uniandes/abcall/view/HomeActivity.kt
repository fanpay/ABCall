package com.uniandes.abcall.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uniandes.abcall.R
import com.uniandes.abcall.data.repository.AuthRepository
import com.uniandes.abcall.data.repository.IncidentRepository
import com.uniandes.abcall.viewmodel.UserViewModel
import com.uniandes.abcall.viewmodel.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(application)
    }
    private lateinit var authRepository: AuthRepository
    private lateinit var incidentsRepository: IncidentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.my_toolbar))


        authRepository = AuthRepository(application)
        incidentsRepository = IncidentRepository(application)

        val token = intent.getStringExtra("TOKEN")
        val userId = intent.getStringExtra("USER_ID")

        Log.e("HomeActivity", "Token: $token")
        Log.e("HomeActivity", "USER_ID: $userId")

        // Usa un Handler para ejecutar el código después de que la vista esté completamente creada
        Handler(Looper.getMainLooper()).post {
            if (userId != null) {
                val navController = findNavController(R.id.nav_host_fragment)
                val bundle = Bundle().apply {
                    putString("USER_ID", userId)
                }
                navController.navigate(R.id.incidentsFragment, bundle)
            } else {
                Log.e("HomeActivity", "El userId es nulo, no se puede cargar el IncidentsFragment")
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    if (userId != null) {
                        val navController = findNavController(R.id.nav_host_fragment)
                        val bundle = Bundle().apply {
                            putString("USER_ID", userId)
                        }
                        navController.navigate(R.id.incidentsFragment, bundle)
                    } else {
                        showNoDataSplash(false, "")
                        showErrorLayout(true, "No se pudo cargar la información del usuario")
                    }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val languageItem: MenuItem? = menu?.findItem(R.id.buttonLanguage)
        val currentLocale = Locale.getDefault().language

        Log.e("HomeActivity", "El idioma a usar es $currentLocale")

        when (currentLocale) {
            "es" -> languageItem?.setIcon(R.drawable.ic_flag_es)
            "en" -> languageItem?.setIcon(R.drawable.ic_flag_us)
            else -> languageItem?.setIcon(R.drawable.ic_flag_xx)
        }

        return true
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

    fun showNoDataSplash(show: Boolean, text: String) {
        val splashNotDataFoundLayout: ConstraintLayout = findViewById(R.id.splash_no_data_found_layout)
        val splashNotDataFoundText: TextView = findViewById(R.id.textViewNotFoundData)

        if (show) {
            splashNotDataFoundLayout.visibility = View.VISIBLE
            splashNotDataFoundText.text = text
        } else {
            splashNotDataFoundLayout.visibility = View.GONE
            splashNotDataFoundText.text = text
        }
    }

    fun showErrorLayout(show: Boolean, text: String) {
        val splashErrorLayout: ConstraintLayout = findViewById(R.id.splash_error_layout)
        val splashErrorLayoutErrorText: TextView = findViewById(R.id.textViewError)

        if (show) {
            splashErrorLayout.visibility = View.VISIBLE
            splashErrorLayoutErrorText.text = text
        } else {
            splashErrorLayout.visibility = View.GONE
            splashErrorLayoutErrorText.text = text
        }
    }
}