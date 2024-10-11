package com.uniandes.abcall.data.database


import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.data.model.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.sql.Timestamp

@RunWith(AndroidJUnit4::class)
class ABCallRoomDatabaseTest {

    private lateinit var database: ABCallRoomDatabase
    private lateinit var usersDao: UsersDao
    private lateinit var incidentsDao: IncidentsDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, ABCallRoomDatabase::class.java
        ).allowMainThreadQueries().build()

        usersDao = database.usersDao()
        incidentsDao = database.incidentsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndGetUser() = runBlocking {
        val user = User(
            id = "1",
            username = "johndoe",
            email = "john.doe@example.com",
            fullName = "John Doe",
            dni = "12345678",
            phoneNumber = "1234567890",
            role = "Admin",
            token = "token_abc123"
        )
        usersDao.insert(user)

        val retrievedUser = usersDao.getUserById("1")
        assertEquals(user, retrievedUser)
    }

    @Test
    fun testInsertAndGetIncident() = runBlocking {
        val incident = Incident(
            id = 1,
            userId = "1",
            subject = "Test Incident",
            description = "This is a test incident",
            originType = "Email",
            status = "OPEN",
            solution = null,
            creationDate = Timestamp(System.currentTimeMillis()),
            updateDate = Timestamp(System.currentTimeMillis()),
            solutionAgentId = null,
            solutionDate = null
        )

        // Insertar el incidente en la base de datos
        incidentsDao.insert(incident)


        // Obtener el valor del LiveData usando getOrAwaitValue
        val retrievedIncidents = incidentsDao.getAllIncidentsSync("1")

        // Verificar que se haya recuperado correctamente
        assertEquals(1, retrievedIncidents.size)
        assertEquals(incident, retrievedIncidents[0])
    }

    @Test
    fun testDeleteAllUsers() = runBlocking {
        val user = User(
            id = "1",
            username = "johndoe",
            email = "john.doe@example.com",
            fullName = "John Doe",
            dni = "12345678",
            phoneNumber = "1234567890",
            role = "Admin",
            token = "token_abc123"
        )
        usersDao.insert(user)
        val deletedCount = usersDao.deleteAll()
        assertEquals(1, deletedCount)
    }

    @Test
    fun testDeleteAllIncidents() = runBlocking {
        val incident = Incident(
            id = 1,
            userId = "1",
            subject = "Test Incident",
            description = "This is a test incident",
            originType = "Email",
            status = "OPEN",
            solution = null,
            creationDate = Timestamp(System.currentTimeMillis()),
            updateDate = Timestamp(System.currentTimeMillis()),
            solutionAgentId = null,
            solutionDate = null
        )
        incidentsDao.insert(incident)
        val deletedCount = incidentsDao.deleteAll()
        assertEquals(1, deletedCount)
    }
}
