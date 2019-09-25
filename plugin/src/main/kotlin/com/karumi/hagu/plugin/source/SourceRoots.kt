package com.karumi.hagu.plugin.source

import com.karumi.hagu.plugin.HaguTask
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

internal fun sources(project: Project): List<Source> {
  project.extensions.findByType(KotlinMultiplatformExtension::class.java)?.let {
    return it.sources()
  }

  val sourceSets = project.property("sourceSets") as SourceSetContainer
  return listOf(Source(
    type = KotlinPlatformType.jvm,
    name = "main",
    sourceSets = listOf("main"),
    sourceDirectorySet = sourceSets.getByName("main").kotlin!!,
    registerTaskDependency = { task ->
      project.tasks.named("compileKotlin").configure { it.dependsOn(task) }
    }
  ))
}

private fun KotlinMultiplatformExtension.sources(): List<Source> =
  targets.flatMap { target ->
    return@flatMap target.compilations.mapNotNull { compilation ->
      if (compilation.name.endsWith(suffix = "Test", ignoreCase = true)) {
        return@mapNotNull null
      }
      Source(
        type = target.platformType,
        konanTarget = (target as? KotlinNativeTarget)?.konanTarget,
        name = "${target.name}${compilation.name.capitalize()}",
        variantName = (compilation as? KotlinJvmAndroidCompilation)?.name,
        sourceDirectorySet = compilation.defaultSourceSet.kotlin,
        sourceSets = compilation.allKotlinSourceSets.map { it.name },
        registerTaskDependency = { task ->
          (target as? KotlinNativeTarget)?.binaries?.forEach {
            it.linkTask.dependsOn(task)
          }
          compilation.compileKotlinTask.dependsOn(task)
        }
      )
    }
  }

internal data class Source(
  val type: KotlinPlatformType,
  val konanTarget: KonanTarget? = null,
  val sourceDirectorySet: SourceDirectorySet,
  val name: String,
  val variantName: String? = null,
  val sourceSets: List<String>,
  val registerTaskDependency: (TaskProvider<HaguTask>) -> Unit
)