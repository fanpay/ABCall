package com.uniandes.abcall.view

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uniandes.abcall.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    private lateinit var scenario: ActivityScenario<HomeActivity>

    @Before
    fun setUp() {
        // Configuramos un Intent con datos de prueba para pasar a la actividad
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, HomeActivity::class.java).apply {
            putExtra("TOKEN", "test_token")
            putExtra("USER_ID", "1")
        }

        // Lanzamos la actividad
        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testNavigationToIncidentsFragment() {
        // Verificamos que la actividad HomeActivity se muestra
        onView(withId(R.id.nav_host_fragment)).check(matches(isDisplayed()))

        // Hacemos clic en el elemento del menú "Home"
        onView(withId(R.id.menu_home)).perform(click())

        // Verificamos que se navega al fragmento de incidentes
        onView(withId(R.id.incidentsFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testLogoutFunctionality() {
        // Hacemos clic en el elemento del menú "Logout"
        onView(withId(R.id.menu_logout)).perform(click())

        // Verificamos que se navega de regreso a MainActivity
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }

    @Test
    fun testShowNoDataSplash() {
        // Simula la llamada para mostrar el splash de "No data"
        scenario.onActivity { activity ->
            activity.showNoDataSplash(true, "No hay datos disponibles")
        }

        // Verificamos que el layout de "No data" es visible y muestra el mensaje correcto
        onView(withId(R.id.splash_no_data_found_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewNotFoundData)).check(matches(withText("No hay datos disponibles")))
    }

    @Test
    fun testShowErrorLayout() {
        // Simula la llamada para mostrar el layout de error
        scenario.onActivity { activity ->
            activity.showErrorLayout(true, "Ocurrió un error")
        }

        // Verificamos que el layout de error es visible y muestra el mensaje correcto
        onView(withId(R.id.splash_error_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewError)).check(matches(withText("Ocurrió un error")))
    }
}
