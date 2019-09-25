package com.karumi.hagu

import com.karumi.hagu.generated.HaguConfig

import org.junit.Test
import junit.framework.TestCase.assertEquals

class AndroidSharedTest {
  
  @Test
  fun `hagu configuration file should be accessible from android`() {
    assertEquals("some_api_key", HaguConfig.API_KEY)
  }

  @Test
  fun `hagu configuration file should contains number key`() {
    assertEquals(11, HaguConfig.NUMBER_KEY)
  }
  
}