plugins {
    kotlin("js")
}

group = "com.github.alombard.mts"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    target {
        compilations.all {
            kotlinOptions.outputFile = "$projectDir/web/simViewApp.js"
            kotlinOptions.moduleKind = "commonjs"
            kotlinOptions.sourceMap = true
            kotlinOptions.sourceMapEmbedSources = "always"
            kotlinOptions.metaInfo = true
        }
        browser {
            webpackTask {
                compilations.all {
                    kotlinOptions.metaInfo = true
                }
                mode = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
                bin = "$projectDir/node_modules/$bin"
            }
        }
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":commons-math"))
                implementation(project(":commons-physics"))
                implementation(project(":commons-utils"))
                implementation(project(":car-model"))
                implementation(project(":car-behavior"))
                implementation(project(":infrastructure-model"))
            }
        }
    }
}
