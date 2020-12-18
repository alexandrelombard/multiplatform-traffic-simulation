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
        browser {}
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":commons-math"))
                implementation(project(":commons-utils"))
                implementation(project(":car-model"))
                implementation(project(":car-behavior"))
                implementation(project(":infrastructure-model"))
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
