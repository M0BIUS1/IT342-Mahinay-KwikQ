package com.example.kwikq

import android.content.Context
import android.widget.Button
import android.widget.EditText
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ProfileActivityRobolectricTest {

    @Test
    fun saveSamplingPersists() {
        val controller = Robolectric.buildActivity(ProfileActivity::class.java).create().start().resume()
        val activity = controller.get()

        val et = activity.findViewById<EditText>(R.id.etSamplingPercent)
        val btn = activity.findViewById<Button>(R.id.btnSaveSampling)
        et.setText("25")
        btn.performClick()

        val prefs = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val stored = prefs.getInt("analytics_sample_percent", -1)
        assertEquals(25, stored)
    }
}
