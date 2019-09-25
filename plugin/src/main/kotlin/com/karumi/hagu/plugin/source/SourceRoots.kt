package com.karumi.hagu.plugin.source

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.karumi.hagu.plugin.BuildHaguTask
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

/**
 * @return A list of source roots and their dependencies.
 *
 * Examples:
 *   Multiplatform Environment. Ios target labeled "ios".
 *     -> iosMain deps [commonMain]
 *
 *   Android environment. internal, production, release, debug variants.
 *     -> internalDebug deps [internal, debug, main]
 *     -> internalRelease deps [internal, release, main]
 *     -> productionDebug deps [production, debug, main]
 *     -> productionRelease deps [production, release, main]
 *
 *    Multiplatform environment with android target (oh boy)
 */
internal fun sources(project: Project): List<Source> {
  // Multiplatform project.
  project.extensions.findByType(KotlinMultiplatformExtension::class.java)?.let {
    return it.sources(project)
  }

  // Kotlin project.
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


private fun KotlinMultiplatformExtension.sources(project: Project): List<Source> {
  // TODO: Look at KotlinPlatformType when we get around to module dependencies and compatibility.
  // We'll probably want to include that in the source so we can tell which source to rely on
  // during dependency resolution.

  return targets
    .flatMap { target ->
      if (target is KotlinAndroidTarget) {
        val extension = project.extensions.getByType(BaseExtension::class.java)
        return@flatMap extension.sources(project)
          .map { source ->
            val compilation = target.compilations.single { it.name == source.name }
            return@map source.copy(
              name = "${target.name}${source.name.capitalize()}",
              sourceSets = source.sourceSets.map { "${target.name}${it.capitalize()}" } + "commonMain",
              registerTaskDependency = { task ->
                compilation.compileKotlinTask.dependsOn(task)
              }
            )
          }
      }
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
}

private fun BaseExtension.sources(project: Project): List<Source> {
  val variants: DomainObjectSet<out BaseVariant> = when (this) {
    is AppExtension -> applicationVariants
    is LibraryExtension -> libraryVariants
    else -> throw IllegalStateException("Unknown Android plugin $this")
  }
  val sourceSets = sourceSets
    .associate { sourceSet ->
      sourceSet.name to sourceSet.kotlin
    }

  return variants.map { variant ->
    Source(
      type = KotlinPlatformType.androidJvm,
      name = variant.name,
      variantName = variant.name,
      sourceDirectorySet = sourceSets[variant.name]
        ?: throw IllegalStateException("Couldnt find ${variant.name} in $sourceSets"),
      sourceSets = variant.sourceSets.map { it.name },
      registerTaskDependency = { task ->
        variant.registerJavaGeneratingTask(task.get(), task.get().generatedSourceOutput)
        project.tasks.named("compile${variant.name.capitalize()}Kotlin").dependsOn(task)
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
  val registerTaskDependency: (TaskProvider<BuildHaguTask>) -> Unit
) {
  fun closestMatch(sources: Collection<Source>): Source? {
    var matches = sources.filter {
      type == it.type || (type == KotlinPlatformType.androidJvm && it.type == KotlinPlatformType.jvm)
    }
    if (matches.size <= 1) return matches.singleOrNull()

    // Multiplatform native matched or android variants matched.
    matches = matches.filter {
      konanTarget == it.konanTarget && variantName == it.variantName
    }
    return matches.singleOrNull()
  }
}