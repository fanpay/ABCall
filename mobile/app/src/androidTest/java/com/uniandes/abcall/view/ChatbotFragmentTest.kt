package com.uniandes.abcall.view

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
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.abcall.R
import com.uniandes.abcall.utils.TestUserCredentials
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale


@RunWith(AndroidJUnit4::class)
class ChatbotFragmentTest {

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
        onView(withId(R.id.fieldUser)).perform(typeText(TestUserCredentials.VALID_USER), closeSoftKeyboard())
        onView(withId(R.id.passField)).perform(typeText(TestUserCredentials.VALID_PASSWORD), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        Thread.sleep(1000)
        intended(hasComponent(HomeActivity::class.java.name))

        Thread.sleep(1000L)
    }

    private fun testNavigateToChatFragment() {
        testLoginSuccess()

        Thread.sleep(1000L)

        onView(withId(R.id.floating_add_incident)).perform(click())

        onView(withId(R.id.btn_yes)).perform(click())

        Thread.sleep(1000L)

    }

    @Test
    fun testChatbotFragmentIsDisplayed() {
        testNavigateToChatFragment()

        onView(withId(R.id.recyclerViewChat)).check(matches(isDisplayed()))
    }

    @Test
    fun testSendMessage() {
        testNavigateToChatFragment()

        val currentLanguage = Locale.getDefault().language
        val userMessage = when (currentLanguage) {
            "es" -> "Hola"
            "en" -> "Hello"
            else -> "Hello"
        }

        onView(withId(R.id.editTextMessage)).perform(typeText(userMessage), closeSoftKeyboard())
        onView(withId(R.id.buttonSend)).perform(click())

        onView(withText(userMessage)).check(matches(isDisplayed()))
    }

    @Test
    fun testReceiveMessage() {
        testNavigateToChatFragment()

        val currentLanguage = Locale.getDefault().language
        val userMessage = when (currentLanguage) {
            "es" -> "crear incidencia"
            "en" -> "create incident"
            else -> "crear incidencia"
        }

        onView(withId(R.id.editTextMessage)).perform(typeText(userMessage), closeSoftKeyboard())
        onView(withId(R.id.buttonSend)).perform(click())

        Thread.sleep(1000L)

        val expectedResponse = when (currentLanguage) {
            "es" -> "necesito más información"
            "en" -> "I need more information"
            else -> "necesito más información"
        }

        onView(withSubstring(expectedResponse)).check(matches(isDisplayed()))
    }
}