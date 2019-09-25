package com.karumi.hagu.plugin

import com.karumi.kotlinsnapshot.matchWithSnapshot
import java.io.File
import java.util.Properties
import junit.framework.TestCase.assertTrue
import org.gradle.internal.impldep.org.testng.annotations.BeforeTest
import org.junit.Test

class KotlinConfigGeneratorTest {

  private val buildDir = File("build/test/generated")
  private val kotlinConfigGenerator = KotlinConfigGenerator(buildDir)

  @BeforeTest
  fun removeDirectories() {
    buildDir.delete()
  }

  @Test
  fun `generate kotlin configuration object with properties`() {
    val properties = Properties()
    properties["property_number"] = "1"
    properties["property_string"] = "\"Hello world\""

    kotlinConfigGenerator.generateConfig(properties)

    readFile(kotlinConfigGenerator.haguConfigFile).matchWithSnapshot()
  }

  @Test
  fun `should create directory if not exist`() {
    kotlinConfigGenerator.generateConfig(Properties())

    assertTrue(kotlinConfigGenerator.generatedSourceOutput.exists())
  }

  @Test
  fun `should create a hagu config file`() {
    kotlinConfigGenerator.generateConfig(Properties())

    assertTrue(kotlinConfigGenerator.haguConfigFile.exists())
  }

  @Test
  fun `should create an empty object if there aren't properties`() {
    kotlinConfigGenerator.generateConfig(Properties())

    readFile(kotlinConfigGenerator.haguConfigFile).matchWithSnapshot()
  }

  private fun readFile(file: File): String = file.readText()
}