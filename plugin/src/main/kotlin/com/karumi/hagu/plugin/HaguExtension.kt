package com.karumi.hagu.plugin

class HaguExtension(var defaultProfile: String = DEFAULT_PROFILE_NAME) {

  companion object {
    const val NAME = "hagu"
    const val DEFAULT_PROFILE_NAME = "gradle"
  }
}