package com.karumi.hagu.multiplatform

import com.karumi.hagu.generated.HaguConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedTest {
  
  @Test
  fun `hagu configuration file should be accessible by common module`() {
    assertEquals("some_api_key", HaguConfig.API_KEY)
  }

  @Test
  fun `hagu configuration file should contains number key`() {
    assertEquals(11, HaguConfig.NUMBER_KEY)
  }

}