import org.jetbrains.kotlin.ir.backend.js.compile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.publish.maven.MavenPom
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
//    id("maven-publish")
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

val artifactName = project.name
val artifactGroup = project.group.toString()
val artifactVersion = project.version.toString()

val pomUrl = "https://github.com/dsd-sztaki-hu/hasura-subset"
val pomScmUrl = "https://github.com/dsd-sztaki-hu/hasura-subset"
val pomIssueUrl = "https://github.com/dsd-sztaki-hu/hasura-subset/issues"
val pomDesc = "https://github.com/dsd-sztaki-hu/hasura-subset"

val githubRepo = "dsd-sztaki-hu/hasura-subset"
val githubReadme = "README.md"

val pomLicenseName = "MIT"
val pomLicenseUrl = "https://opensource.org/licenses/mit-license.php"
val pomLicenseDist = "repo"

val pomDeveloperId = "beepsoft"
val pomDeveloperName = "Balazs E. Pataki"

group = "hu.sztaki.dsd"
version = "0.1.0"




println("project: $project")

// Reference gradle.properties
val ktor_version: String by project
val serialization_version: String by project


repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/ktor")
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://jitpack.io")
}
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> {
            macosX64("native") {
                binaries.all {
                    // https://kotlinlang.org/docs/reference/mpp-dsl-reference.html#native-targets
                    // Make sure we use libs from brew (In my install macOS finds /opt/lib first (MacPorts libs), but
                    // we want to use brew's curl)
                    linkerOpts = mutableListOf("-L/usr/lib/", "-lcurl")
                }
            }

        }
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.ktor:ktor-client-mock:$ktor_version")

            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktor_version")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-curl:$ktor_version")
            }
        }
        val nativeTest by getting
    }

    dependencies {
    }
}

// Adapted from: https://github.com/lamba92/kotlin-multiplatform-coroutines-runtest/blob/master/build.gradle.kts
bintray {
    // Provide these as ENV vars
    user = searchPropertyOrNull("bintrayUsername")
    key = searchPropertyOrNull("bintrayApiKey")
    pkg {
        version {
            name = project.version.toString()
        }
        userOrg = "dsd-sztaki-hu"
        repo = "dsd-oss"
        name = "hasura-subset"
        setLicenses("MIT")
        vcsUrl = "https://github.com/dsd-sztaki-hu/hasura-subset"
        issueTrackerUrl = "https://github.com/dsd-sztaki-hu/hasura-subset/issues"
    }
    publish = true
    setPublications("jvm", "js", "macos-x64", "kotlinMultiplatform")
//    if (OperatingSystem.current().isMacOsX)
//        setPublications("jvm", "js",/* "macos-x64", "ios-arm64", "ios-arm32", "linux-x64",*/ "kotlinMultiplatform")
//    else (OperatingSystem.current().isWindows)
//        setPublications("windows-x64")
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

fun searchPropertyOrNull(propertyName: String): String? =
    project.findProperty(propertyName) as String? ?: System.getenv(propertyName)

fun BintrayExtension.pkg(action: BintrayExtension.PackageConfig.() -> Unit) {
    pkg(closureOf(action))
}

fun BintrayExtension.PackageConfig.version(action: BintrayExtension.VersionConfig.() -> Unit) {
    version(closureOf(action))
}
