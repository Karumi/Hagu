package com.karumi.hagu

import com.karumi.hagu.generated.HaguConfig

import org.junit.Test
import junit.framework.TestCase.assertEquals

class AndroidSharedCIProfileTest {

  @Test
  fun `hagu configuration file should be accessible from android`() {
    assertEquals("ci_api_key", HaguConfig.API_KEY)
  }

  @Test
  fun `hagu configuration file should contains number key`() {
    assertEquals(12, HaguConfig.NUMBER_KEY)
  }
}