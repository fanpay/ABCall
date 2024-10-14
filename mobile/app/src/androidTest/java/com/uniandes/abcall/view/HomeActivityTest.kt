package com.uniandes.abcall.view

import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.uniandes.abcall.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
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
            putExtra("TOKEN", "2e9ee56d-0f76-4595-8cdb-3bfefd084749")
            putExtra("USER_ID", "b523bf06-d11b-4f44-b04a-08f568ddcfab")
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
        onView(withIndex(withId(R.id.nav_host_fragment), 0)).check(matches(isDisplayed()))

        // Hacemos clic en el elemento del menú "Home"
        onView(withId(R.id.menu_home)).perform(click())

        Thread.sleep(1000)

        // Verificamos que se navega al fragmento de incidentes
        onView(withId(R.id.incidentsRv)).check(matches(isDisplayed()))
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

    private fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var currentIndex = 0
            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }
}
