package com.karumi.example

import com.karumi.hagu.generated.HaguConfig
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MainTest {

  @Test
  fun `hagu configuration file should contains string key`() {
    assertEquals("some_api_key", HaguConfig.API_KEY)
  }

  @Test
  fun `hagu configuration file should contains number key`() {
    assertEquals(11, HaguConfig.NUMBER_KEY)
  }

}