package com.uniandes.abcall.view

import android.app.Application
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.abcall.R
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val testUser = "deshawn"
    private val testPwd = "n1jk1f3iudtnacu"
    private val wrongUser = "wrongUser"
    private val wrongPwd = "wrongPassword"

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testLoginSuccess() {

        onView(withId(R.id.fieldUser)).perform(typeText(testUser))
        onView(withId(R.id.fieldUser)).perform(closeSoftKeyboard())

        onView(withId(R.id.passField)).perform(typeText(testPwd))
        onView(withId(R.id.passField)).perform(closeSoftKeyboard())

        onView(withId(R.id.btn_login)).perform(click())

        // Verifica que se navega a HomeActivity
        Thread.sleep(1000)
        intended(hasComponent(HomeActivity::class.java.name))

        onView(withId(R.id.my_toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginFailure() {
        onView(withId(R.id.fieldUser)).perform(typeText(wrongUser), closeSoftKeyboard())
        onView(withId(R.id.passField)).perform(typeText(wrongPwd), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        val expectedTitle = getApplicationContext<Application>().getString(R.string.error_login)
        onView(withText(containsString(expectedTitle)))
            .check(matches(isDisplayed()))
    }
}
