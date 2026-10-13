package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("HyperBoost AI Game Studio", appName)
  }

  @Test
  fun `verify sensitivity engine calculation`() {
    val config = com.example.service.SensitivityEngine.calculateMobileSensitivity(800, 1.5f, "PUBG")
    org.junit.Assert.assertTrue(config.convertedGeneralSens > 0f)
    org.junit.Assert.assertTrue(config.convertedGyroSens > 100f)
  }
}
