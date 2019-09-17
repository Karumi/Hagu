package com.karumi.hagu.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.FileInputStream
import java.util.*

sealed class HaguTasks : DefaultTask() {

    init {
        group = "Hagu"
    }

}

open class BuildHagu : HaguTasks() {

    companion object {
        const val NAME = "buildHagu"
        private const val PROPERTIES_CONFIG = "gradle.properties"
    }

    init {
        description = "Build Kotlin configuration from configuration file."
    }

    @TaskAction
    fun build() {
        val kotlinConfigGenerator = KotlinConfigGenerator(project.buildDir)
        kotlinConfigGenerator.generateConfig(getProperties())
    }

    private fun getProperties(): Properties {
        val localPropertiesFile = project.file(PROPERTIES_CONFIG)
        val localProperties = Properties()
        localProperties.load(FileInputStream(localPropertiesFile))
        return localProperties
    }
}