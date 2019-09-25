package com.karumi.hagu.multiplatform

import com.karumi.hagu.generated.HaguConfig

expect fun logInfo(tag: String, message: String)

fun doThingsWithGeneratedCode() {
  logInfo("Multiplatform", "API_KEY = ${HaguConfig.API_KEY}")
  logInfo("Multiplatform", "API_KEY = ${HaguConfig.NUMBER_KEY}")
}