package com.uniandes.abcall

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.abcall.view.HomeActivity
import com.uniandes.abcall.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testLoginSuccess() {
        onView(withId(R.id.fieldUser)).perform(typeText("testUser"))
        onView(withId(R.id.passField)).perform(typeText("password"))
        onView(withId(R.id.btn_login)).perform(click())

        // Verifica que se navega a HomeActivity
        intended(hasComponent(HomeActivity::class.java.name))
        onView(withId(R.id.fieldUser)).check(matches(withText("Bienvenido, testUser")))
    }

    @Test
    fun testLoginFailure() {
        onView(withId(R.id.fieldUser)).perform(typeText("wrongUser"))
        onView(withId(R.id.passField)).perform(typeText("wrongPassword"))
        onView(withId(R.id.btn_login)).perform(click())

        // Verifica el mensaje de error
        onView(withText("Error de autenticación")).check(matches(isDisplayed()))
    }
}
