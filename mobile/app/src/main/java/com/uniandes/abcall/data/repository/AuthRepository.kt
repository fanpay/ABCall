package com.uniandes.abcall.data.repository

import android.app.Application
import android.util.Log
import com.uniandes.abcall.R
import com.uniandes.abcall.data.database.ABCallRoomDatabase
import com.uniandes.abcall.data.database.UsersDao
import com.uniandes.abcall.data.exceptions.ApiRequestException
import com.uniandes.abcall.data.model.AuthResponse
import com.uniandes.abcall.data.model.User
import com.uniandes.abcall.data.model.UserCredentials
import com.uniandes.abcall.data.service.RetrofitBroker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AuthRepository(val application: Application) {
    private val usersDao: UsersDao by lazy {
        ABCallRoomDatabase.getDatabase(application).usersDao()
    }

    // Método para llamar al servicio de autenticación
    suspend fun login(userCredentials: UserCredentials): AuthResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitBroker.login(userCredentials)
                Log.e("AuthRepository", "Respuesta de login: $response")
                response
            } catch (e: Throwable) {
                if (e is HttpException) {
                    when (e.code()) {
                        404 -> {
                            throw ApiRequestException(application.getString(R.string.user_not_found))
                        }
                        else -> {
                            Log.e("AuthRepository", "Error durante el login: ${e.message}")
                            throw ApiRequestException(
                                application.resources.getString(R.string.error_login),
                                e
                            )
                        }
                    }
                } else {
                    // Otras excepciones
                    Log.e("AuthRepository", "Error durante el login: ${e.message}")
                    throw ApiRequestException(
                        application.resources.getString(R.string.error_login),
                        e
                    )
                }
            }
        }
    }

    suspend fun fetchAndStoreUserInfo(token: String) {
        withContext(Dispatchers.IO) {
            try {
                val user = RetrofitBroker.getUserInfo(token)

                val userWithToken = user.copy(token = token)  // Crear una copia del usuario con el token
                // Guardar el usuario en Room
                usersDao.insert(userWithToken)

                Log.d("AuthRepository", "Usuario guardado en Room: $user")
            } catch (e: Throwable) {
                Log.e("AuthRepository", "Error al obtener los datos del usuario: ${e.message}")
                throw ApiRequestException(
                    application.getString(R.string.error_fetch_user_info),
                    e
                )
            }
        }
    }

    suspend fun getStoredUser(id: String): User? {
        return withContext(Dispatchers.IO) {
            usersDao.getUserById(id)
        }
    }

    suspend fun clearUsersTable() {
        withContext(Dispatchers.IO) {
            usersDao.deleteAll()
        }
    }
}