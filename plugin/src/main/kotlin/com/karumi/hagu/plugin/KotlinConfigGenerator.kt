package com.karumi.hagu.plugin

import java.io.File
import java.util.Properties

val configurationFile = """
    package com.karumi.hagu.generated
    
    object HaguConfig {
        &properties
        
    }  

""".trimIndent()

class KotlinConfigGenerator(
  buildConfigDir: File
) {

  companion object {
    private const val HAGU_CONFIG_FILE_NAME = "HaguConfig.kt"
    private const val HAGU_GENERATED_SOURCE_FOLDER = "generated/kotlin/config"
    private const val PROPERTIES = "&properties"
  }

  val generatedSourceOutput = File(
    buildConfigDir,
    HAGU_GENERATED_SOURCE_FOLDER
  )
  val haguConfigFile = File(
    generatedSourceOutput,
    HAGU_CONFIG_FILE_NAME
  )

  fun generateConfig(properties: Properties) {
    createGeneratedSourcesIfNeeded()

    val propertiesVariables = properties.toList().fold("") { accumulation, property ->
      val key = property.first.toString().toUpperCase()
      accumulation + "\n\tconst val $key = ${property.second}"
    }

    haguConfigFile.writeText(getConfigContent(propertiesVariables))
  }

  private fun createGeneratedSourcesIfNeeded() {
    if (!generatedSourceOutput.exists()) {
      generatedSourceOutput.mkdirs()
    }
  }

  private fun getConfigContent(propertiesVariables: String) =
    configurationFile
      .replace(PROPERTIES, propertiesVariables)
}