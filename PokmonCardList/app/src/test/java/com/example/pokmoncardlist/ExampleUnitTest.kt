package com.example.pokmoncardlist

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Ordering
import org.junit.runners.JUnit4

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    // Main Activity (Search Activity)
    @Test
    fun buildQuery_1() {
        val request = MainActivity().buildQuery(1, "charizard", "", "")
        assertEquals("https://api.pokemontcg.io/v2/cards?q=name:*charizard* ", request)
    }

    @Test
    fun buildQuery_2() {
        val request = MainActivity().buildQuery(1, "", "ex14", "")
        assertEquals("https://api.pokemontcg.io/v2/cards?q=set.id:ex14 ", request)
    }

    @Test
    fun buildQuery_3() {
        val request = MainActivity().buildQuery(1, "", "", "Water")
        assertEquals("https://api.pokemontcg.io/v2/cards?q=types:Water ", request)
    }

    @Test
    fun buildQuery_4() {
        val request = MainActivity().buildQuery(1, "char", "ex14", "")
        assertEquals("https://api.pokemontcg.io/v2/cards?q=name:*char* set.id:ex14 ", request)
    }

    // Collection Activity
    @Test
    fun deletePromopt_1() {
        val delCards = CollectionActivity().deletePrompt(1)
        assertEquals(0, delCards)
    }

    // Camera Activity
    @Test
    fun setRatings_1() {
        val ratings = CameraActivity().setRatings(3.5f, 4.0f, 3.0f, 2.5f, 0)
        assertEquals(listOf<Float>(3.5f, 4.0f, 3.0f, 2.5f), ratings)
    }

    @Test
    fun setRatings_2() {
        val ratings = CameraActivity().setRatings(5.0f, 5.0f, 5.0f, 5.0f, 1)
        assertEquals(listOf<Float>(5.0f, 5.0f, 5.0f, 5.0f), ratings)
    }

    @Test
    fun setRatings_3() {
        val ratings = CameraActivity().setRatings(0.0f, 0.0f, 0.0f, 0.0f, 2)
        assertEquals(listOf<Float>(0.0f, 0.0f, 0.0f, 0.0f), ratings)
    }

}