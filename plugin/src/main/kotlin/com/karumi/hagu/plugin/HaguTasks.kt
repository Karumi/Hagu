package com.karumi.hagu.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileInputStream
import java.util.Properties

@CacheableTask
open class BuildHaguTask : DefaultTask() {

  companion object {
    const val NAME = "buildHagu"
    private const val PROPERTIES_CONFIG = "gradle.properties"
  }

  init {
    group = "Hagu"
    description = "Build Kotlin configuration from configuration file."
  }

  @get:OutputDirectory
  @get:PathSensitive(PathSensitivity.RELATIVE)
  var generatedSourceOutput: File? = null

  @TaskAction
  fun build() {
    generatedSourceOutput?.run {
      deleteRecursively()
      val kotlinConfigGenerator = KotlinConfigGenerator(this)
      kotlinConfigGenerator.generateConfig(getProperties())
    }
  }

  private fun getProperties(): Properties {
    val localPropertiesFile = project.file(PROPERTIES_CONFIG)
    val localProperties = Properties()
    localProperties.load(FileInputStream(localPropertiesFile))
    return localProperties
  }
}