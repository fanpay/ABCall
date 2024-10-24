package com.uniandes.abcall.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.abcall.R
import com.uniandes.abcall.utils.TestUserCredentials
import com.uniandes.abcall.view.adapters.IncidentAdapter
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class IncidentDetailFragmentTest {
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

    private fun testLoginSuccess() {
        onView(withId(R.id.fieldUser)).perform(typeText(TestUserCredentials.validUser), closeSoftKeyboard())
        onView(withId(R.id.passField)).perform(typeText(TestUserCredentials.validPassword), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        Thread.sleep(1000)
        intended(hasComponent(HomeActivity::class.java.name))

        onView(withId(R.id.incidentsRv))
            .perform(RecyclerViewActions.actionOnItemAtPosition<IncidentAdapter.IncidentViewHolder>(0, click()))

        Thread.sleep(3000L)
    }

    @Test
    fun testRecyclerViewItemClick() {
        // Usa el mismo flujo de login exitoso
        testLoginSuccess()

        onView(withId(R.id.incident_image)).check(matches(isDisplayed()))
        onView(withId(R.id.incident_subject)).check(matches(isDisplayed()))
        onView(withId(R.id.incidentStatus)).check(matches(isDisplayed()))
        onView(withId(R.id.incidentID)).check(matches(isDisplayed()))
        onView(withId(R.id.incidentCreationDate)).check(matches(isDisplayed()))

        Thread.sleep(2000L)
    }
}