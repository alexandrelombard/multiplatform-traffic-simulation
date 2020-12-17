plugins {
    kotlin("jvm")
}

group = "com.github.alombard.mts"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation("no.tornado:tornadofx:1.7.20")

                implementation(project(":commons-math"))
                implementation(project(":commons-physics"))
                implementation(project(":infrastructure-model"))
                implementation(project(":car-model"))
                implementation(project(":car-behavior"))
                implementation(project(":traffic-simulation"))
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
