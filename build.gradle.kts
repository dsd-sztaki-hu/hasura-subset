import org.jetbrains.kotlin.ir.backend.js.compile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"

}
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


//    dependencies {
//        testImplementation("io.ktor:ktor-client-mock:$ktor_version")
//    }


    dependencies {
        //compile("io.ktor:1.4.0")
//        implementation("io.ktor:ktor-server-netty:$ktor_version")
//        implementation("ch.qos.logback:logback-classic:$logback_version")
//        commonMainImplementation("io.ktor:ktor-client-core:$ktor_version")
//        commonMainImplementation("io.ktor:ktor-client-core-jvm:$ktor_version")
//        commonMainImplementation("io.ktor:ktor-client-cio:$ktor_version")
//        commonMainImplementation("io.ktor:ktor-client-http-timeout:$ktor_version")
//        implementation("io.ktor:ktor-client-auth-jvm:$ktor_version")
//        implementation("io.ktor:ktor-client-json-jvm:$ktor_version")
//        implementation("io.ktor:ktor-client-gson:$ktor_version")
//        implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
//        testImplementation("io.ktor:ktor-server-tests:$ktor_version")
//        testImplementation("io.ktor:ktor-client-mock:$ktor_version")
//        testImplementation("io.ktor:ktor-client-mock-jvm:$ktor_version")
//        commonTestImplementation("io.ktor:ktor-client-mock:$ktor_version")
    }
}


// https://medium.com/mindorks/migrating-gradle-build-scripts-to-kotlin-dsl-89788a4e383a
//group 'Example'
//version '1.0-SNAPSHOT'
//
//buildscript {
//    ext.kotlin_version = '1.4.0'
//    ext.ktor_version = '1.4.0'
//
//    repositories {
//        mavenCentral()
//    }
//    dependencies {
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
//    }
//}
//
//apply plugin: 'java'
//apply plugin: 'kotlin'
//
//sourceCompatibility = 1.8
//compileKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//compileTestKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//
//kotlin {
//    experimental {
//        coroutines "enable"
//    }
//}
//
//repositories {
//    jcenter()
//}
//
//dependencies {
//    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
//    compile "io.ktor:ktor-server-netty:$ktor_version"
//    testCompile group: 'junit', name: 'junit', version: '4.12'
//}
