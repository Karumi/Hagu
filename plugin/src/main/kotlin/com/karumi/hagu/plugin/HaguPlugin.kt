package com.karumi.hagu.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class HaguPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.afterEvaluate(::addTasks)
  }

  private fun addTasks(project: Project) {
    val hagoBuildTask = project.tasks.create(
      BuildHagu.NAME,
      BuildHagu::class.java
    )
    val buildTask = project.tasks.findByName("compileKotlin")
    buildTask?.dependsOn(hagoBuildTask)
  }
}