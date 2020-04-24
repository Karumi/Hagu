package com.karumi.example

import com.karumi.hagu.generated.HaguConfig
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DebugConfigFromExtensionTest {

  @Test
  fun `hagu configuration file should contains string key`() {
    assertEquals("this is a debug key", HaguConfig.API_KEY)
  }

  @Test
  fun `hagu configuration file should contains number key`() {
    assertEquals(12, HaguConfig.NUMBER_KEY)
  }
}