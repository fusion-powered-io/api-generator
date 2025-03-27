import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin.Companion.kotlinNodeJsEnvSpec
import java.nio.file.Files
import kotlin.io.path.absolutePathString

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.kotest)
}

val libraryName = "@fusionpowered/$name"

kotlin {
  js {
    outputModuleName = libraryName
    version = "1.0.0"
    nodejs {
      val main by compilations.getting {
        packageJson {
          main = "kotlin/index.js"
        }
      }

      kotlinNodeJsEnvSpec.download = true
      kotlinNodeJsEnvSpec.version = "22.14.0"
      kotlinNodeJsEnvSpec.installationDirectory = projectDir.resolve(".gradle")
    }
    compilerOptions {
      target = "es2015"
      optIn.addAll(
        "kotlin.js.ExperimentalJsExport",
        "kotlin.js.ExperimentalJsCollectionsApi",
        "kotlinx.coroutines.DelicateCoroutinesApi"
      )
    }
    binaries.executable()
  }

  sourceSets {
    jsMain.dependencies {
      implementation(libs.bundles.kotlinx)
      implementation(libs.bundles.kotlin.wrapper)
      implementation(npm("@asyncapi/avro-schema-parser", "3.0.24"))
      implementation(npm("@asyncapi/parser", "3.3.0"))
      implementation(npm("@eventcatalog/sdk", "2.2.7"))
      implementation(npm("@apidevtools/swagger-parser", "10.1.0"))
      implementation(npm("chalk", "4"))
      implementation(npm("slugify", "1.6.6"))
    }

    jsTest.dependencies {
      implementation(libs.bundles.kotest)
    }
  }
}

val npmPack = tasks.register<Exec>("npmPack") {
  doFirst {
    val npm = projectDir.resolve(".gradle").toPath()
      .let { gradlePath -> Files.find(gradlePath, 5, { path, _ -> path.fileName.endsWith("npm") }) }
      .filter { possibleNpmPath -> possibleNpmPath.absolutePathString().contains(kotlinNodeJsEnvSpec.version.get()) }
      .findFirst().get()
      .absolutePathString()

    workingDir = projectDir.resolve("build/js/packages/$libraryName")
    commandLine = listOf(npm, "pack", "--pack-destination=${projectDir.resolve("build")}")
  }
}

tasks.build {
  dependsOn("kotlinNodeJsSetup")
  finalizedBy(npmPack)
}
