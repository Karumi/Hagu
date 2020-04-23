package com.karumi.hagu.plugin

import com.karumi.hagu.plugin.source.sources
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

class HaguPlugin : Plugin<Project> {

  companion object {
    private const val HAGU_GENERATED_SOURCE_FOLDER = "generated/kotlin/config"
    private const val PROFILE_PROPERTY_NAME = "profile"
  }

  override fun apply(project: Project) {
    project.extensions.add(HaguExtension.NAME, HaguExtension())
    project.afterEvaluate(::addTasks)
  }

  private fun addTasks(project: Project) {
    val profile = getTargetProfile(project)
    val outputDirectory = File(project.buildDir, HAGU_GENERATED_SOURCE_FOLDER)
    val task = project.tasks.register(
      HaguTask.NAME,
      HaguTask::class.java
    ) { task ->
      task.generatedSourceOutput = outputDirectory
      task.profile = profile
    }

    val sources = sources(project)

    val common = sources.singleOrNull { it.type == KotlinPlatformType.common }
    common?.sourceDirectorySet?.srcDir(outputDirectory.toRelativeString(project.projectDir))

    sources.forEach { source ->
      if (common == null) {
        source.sourceDirectorySet.srcDir(outputDirectory.toRelativeString(project.projectDir))
      }

      source.registerTaskDependency(task)
    }
  }

  private fun getTargetProfile(project: Project): String =
    if (project.hasProperty(PROFILE_PROPERTY_NAME)) {
      project.property(PROFILE_PROPERTY_NAME) as String
    } else {
      val extension = project.extensions.getByName(HaguExtension.NAME) as HaguExtension
      extension.defaultProfile
    }
}