package com.karumi.hagu.plugin

import com.karumi.hagu.plugin.HaguExtension.Companion.DEFAULT_PROFILE_NAME
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
open class HaguTask : DefaultTask() {

  companion object {
    const val NAME = "buildHagu"
  }

  init {
    group = "Hagu"
    description = "Build Kotlin configuration from configuration file."
  }

  @get:OutputDirectory
  @get:PathSensitive(PathSensitivity.RELATIVE)
  var generatedSourceOutput: File? = null
  var profile: String = DEFAULT_PROFILE_NAME

  @TaskAction
  fun build() {
    generatedSourceOutput?.run {
      print("Building Hagu configuration for profile: $profile.properties")

      deleteRecursively()
      val kotlinConfigGenerator = KotlinConfigGenerator(this)
      kotlinConfigGenerator.generateConfig(getProperties())
    }
  }

  private fun getProperties(): Properties {
    val localPropertiesFile = project.file("$profile.properties")
    val localProperties = Properties()
    localProperties.load(FileInputStream(localPropertiesFile))
    return localProperties
  }
}