package com.karumi.hagu.multiplatform

import com.karumi.hagu.generated.HaguConfig
import platform.Foundation.NSLog

actual fun logInfo(tag: String, message: String) {
  NSLog("$tag: $message")
}

fun doThingsWithGeneratedIOSCode() {
  logInfo("Multiplatform", "API_KEY = ${HaguConfig.API_KEY}")
  logInfo("Multiplatform", "API_KEY = ${HaguConfig.NUMBER_KEY}")
}