package com.karumi.hagu.plugin

import com.karumi.hagu.plugin.source.sources
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import java.io.File

class HaguPlugin : Plugin<Project> {

  companion object {
    private const val HAGU_GENERATED_SOURCE_FOLDER = "generated/kotlin/config"
  }

  override fun apply(project: Project) {
    project.afterEvaluate(::addTasks)
  }

  private fun addTasks(project: Project) {
    val outputDirectory = File(project.buildDir, HAGU_GENERATED_SOURCE_FOLDER)
    val task = project.tasks.register(
      BuildHaguTask.NAME,
      BuildHaguTask::class.java
    ) {
      it.generatedSourceOutput = outputDirectory
    }

    val sources = sources(project)

    val common = sources.singleOrNull { it.type == KotlinPlatformType.common }
    common?.sourceDirectorySet?.srcDir(outputDirectory.toRelativeString(project.projectDir))

    sources.forEach { source ->
      // Add the source dependency on the generated code.
      if (common == null) {
        source.sourceDirectorySet.srcDir(outputDirectory.toRelativeString(project.projectDir))
      }

      source.registerTaskDependency(task)
    }
  }
}