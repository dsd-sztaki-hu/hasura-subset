import org.jetbrains.kotlin.ir.backend.js.compile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.publish.maven.MavenPom
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.internal.os.OperatingSystem

// Reference gradle.properties
val kotlin_version: String by project
val ktor_version: String by project
val kor_version: String by project
val serialization_version: String by project
val graphql_java_version: String by project

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

plugins {
    kotlin("multiplatform") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"
}

println("project: $project")


repositories {
    mavenCentral()
    jcenter()
//    maven("https://kotlin.bintray.com/ktor")
//    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://jitpack.io")
//    maven("https://dl.bintray.com/korlibs/korlibs/")
}

kotlin {
    // https://youtrack.jetbrains.com/issue/KTOR-2055
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-stdlib")) {
                useVersion(kotlin_version)
            }
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
//            kotlinOptions.useIR = true
        }
    }
    // with js(IR) korio fails
    js(LEGACY) {
        useCommonJs()
        browser {
            webpackTask {
                output.libraryTarget = "commonjs2"
            }
            binaries.executable()
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        nodejs ()
    }
//    val hostOs = System.getProperty("os.name")
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" -> {
//            macosX64("native") {
//                binaries.all {
//                    // https://kotlinlang.org/docs/reference/mpp-dsl-reference.html#native-targets
//                    // Make sure we use libs from brew (In my install macOS finds /opt/lib first (MacPorts libs), but
//                    // we want to use brew's curl)
//                    linkerOpts = mutableListOf("-L/usr/lib/", "-lcurl")
//                }
//            }
//
//        }
//        hostOs == "Linux" -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }

    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
                implementation("com.soywiz.korlibs.korio:korio:$kor_version")

//                implementation("asd:qwe:1.0.0")
//s                implementation("com.kgbier.graphql:graphql-parser:1.0.0")
//                implementation("com.kgbier.graphql:graphql-parser-jvm:1.0.0")
//                implementation("com.kgbier.graphql:graphql-parser-js:1.0.0")
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
                implementation("com.graphql-java:graphql-java:$graphql_java_version")
                implementation("com.fasterxml.jackson.core:jackson-core:2.12.1")
                implementation("com.fasterxml.jackson.core:jackson-databind:2.12.1")
                implementation(kotlin("reflect"))
            }

        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                // Dukat unable to generate proper typings as of now so we ignore it
                implementation(npm("graphql", "15.5.0", generateExternals = false))
                implementation(npm("graphql-2-json-schema", "0.5.1", generateExternals = true))
                implementation("com.soywiz.korlibs.korio:korio-js:$kor_version")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
//        val nativeMain by getting {
//            dependencies {
//                implementation("io.ktor:ktor-client-curl:$ktor_version")
//            }
//        }
//        val nativeTest by getting
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
