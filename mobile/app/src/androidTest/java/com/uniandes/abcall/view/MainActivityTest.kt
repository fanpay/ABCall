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
import com.uniandes.abcall.utils.TestUserCredentials
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
        onView(withId(R.id.fieldUser)).perform(typeText(TestUserCredentials.VALID_USER), closeSoftKeyboard())
        onView(withId(R.id.passField)).perform(typeText(TestUserCredentials.VALID_PASSWORD), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        Thread.sleep(1000)
        intended(hasComponent(HomeActivity::class.java.name))

        onView(withId(R.id.my_toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginFailure() {
        onView(withId(R.id.fieldUser)).perform(typeText(TestUserCredentials.INVALID_USER), closeSoftKeyboard())
        onView(withId(R.id.passField)).perform(typeText(TestUserCredentials.INVALID_PASSWORD), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        val expectedTitle = getApplicationContext<Application>().getString(R.string.error_login)
        onView(withText(containsString(expectedTitle)))
            .check(matches(isDisplayed()))
    }
}
