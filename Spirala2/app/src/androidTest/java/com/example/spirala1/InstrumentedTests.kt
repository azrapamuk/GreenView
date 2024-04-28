package com.example.spirala1

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.PositionAssertions
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyRightOf
import androidx.test.espresso.assertion.PositionAssertions.isLeftAlignedWith
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.hamcrest.Description
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentedTests {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(
        NovaBiljkaActivity::class.java
    )

    private lateinit var editText: EditText
    private lateinit var listView: ListView

    @Test
    fun testNazivPrekratak() {
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.nazivET)
        }
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        editText.setText("p")
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText, "Prekratak naziv")
    }

    @Test
    fun testNazivPredug() {
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.nazivET)
        }
        editText.setText("ovaj naziv je sigurno predug")
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText, "Predug naziv")
    }

    @Test
    fun testNazivTacan() {
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.nazivET)
        }
        editText.setText("Biljka")
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNull(errorText)
    }

    @Test
    fun testPorodicaPrekratak() {
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.porodicaET)
        }
        editText.setText("p")
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText, "Prekratak naziv")
    }

    @Test
    fun testPorodicaPredug() {
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.porodicaET)
        }
        editText.setText("ovaj naziv je sigurno predug")
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText, "Predug naziv")
    }

    @Test
    fun testPorodicaTacan() {
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.porodicaET)
        }
        editText.setText("Porodica")
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNull(errorText)
    }

    @Test
    fun testUpozorenjePrekratak() {
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.medicinskoUpozorenjeET)
        }
        editText.setText("p")
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText)
    }

    @Test
    fun testUpozorenjePredug() {
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.medicinskoUpozorenjeET)
        }
        editText.setText("ovaj naziv je sigurno predug")
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText, "Predug naziv")
    }

    @Test
    fun testUpozorenjeTacan() {
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.medicinskoUpozorenjeET)
        }
        editText.setText("Upozorenje")
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNull(errorText)
    }

    @Test
    fun testJeloPrekratko(){
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.jeloET)
            listView=it.findViewById(R.id.jelaLV)
        }
        editText.setText("a")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText, "Naziv prekratak")
        val s=listView.count
        assertEquals(s,0)
    }

    @Test
    fun testJeloPredug(){
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.jeloET)
            listView=it.findViewById(R.id.jelaLV)
        }
        editText.setText("naziv ovog jela je predug")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText, "Naziv predug")
        val s=listView.count
        assertEquals(s,0)
    }

    @Test
    fun testJeloTacno(){
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.jeloET)
            listView=it.findViewById(R.id.jelaLV)
        }
        editText.setText("Grah")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNull(errorText)
        val s=listView.count
        assertEquals(s,1)

    }

    @Test
    fun testJeloDuplikat(){
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.jeloET)
            listView=it.findViewById(R.id.jelaLV)
        }
        editText.setText("Grah")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        editText.setText("grah")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText, "Jelo vec postoji")
        val s=listView.count
        assertEquals(s,1)
    }

    @Test
    fun testJeloIzmjena(){
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.jeloET)
            listView=it.findViewById(R.id.jelaLV)
        }
        editText.setText("Grah")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withText("Grah")).perform(click())
        editText.setText("Gulas")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNull(errorText)
        val s=listView.count
        assertEquals(s,1)
    }

    @Test
    fun testJeloIzmjenaDuplikat(){
        val activity = activityScenarioRule.scenario.onActivity{
            editText=it.findViewById<EditText>(R.id.jeloET)
            listView=it.findViewById(R.id.jelaLV)
        }
        editText.setText("Grah")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        editText.setText("Pasta")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        editText.setText("Gulas")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jelaLV)).perform(click())
        editText.setText("gulas")
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        val errorText = editText.error?.toString()
        assertNotNull(errorText)
        val s=listView.count
        assertEquals(s,3)
    }

}