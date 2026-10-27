package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CheckInRecord
import com.example.ui.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string resource matches CampusWell`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CampusWell", appName)
    }

    @Test
    fun `verify screen items initialization and routes`() {
        val studentItems = Screen.studentItems
        assertEquals(5, studentItems.size)
        studentItems.forEach { screen ->
            assertNotNull("Screen must not be null", screen)
            assertTrue("Screen route must not be blank", screen.route.isNotBlank())
            assertTrue("Screen title must not be blank", screen.title.isNotBlank())
        }

        val facultyItems = Screen.facultyItems
        assertEquals(4, facultyItems.size)
        facultyItems.forEach { screen ->
            assertNotNull("Faculty screen must not be null", screen)
            assertTrue("Faculty screen route must not be blank", screen.route.isNotBlank())
        }
    }

    @Test
    fun `verify check-in metric calculations`() {
        val record = CheckInRecord(
            id = "test-1",
            date = "2025-04-28",
            dayOfWeek = "Mon",
            mood = "Good",
            sleepHours = 7.5f,
            waterGlasses = 6,
            studyHours = 4.5f,
            stressLevel = 2
        )

        assertEquals(1.5f, record.waterLiters, 0.001f)
        assertEquals("7h 30m", record.sleepFormatted)
        assertEquals("4h 30m", record.studyFormatted)
    }
}
