plugins {
    kotlin("multiplatform")
}

group = "com.github.alombard.mts"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons-utils"))
                implementation(project(":commons-simulation"))
                implementation(project(":infrastructure-model"))
                implementation(project(":v2x-simulation"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
