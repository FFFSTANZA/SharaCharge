plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

group = "com.SharaSpot.buildlogic"

// Configure the build-logic plugins to target JDK 18
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_18
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "18"
    }
}


dependencies {
    compileOnly(libs.build.gradle)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApp") {
            id = "SharaSpot.application"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("androidLibrary") {
            id = "SharaSpot.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("koin") {
            id = "SharaSpot.koin"
            implementationClass = "KoinConventionPlugin"
        }
        register("compose") {
            id = "SharaSpot.compose"
            implementationClass = "LibraryComposeConventionPlugin"
        }
        register("appCompose") {
            id = "SharaSpot.application.compose"
            implementationClass = "AppComposeConventionPlugin"
        }
        register("androidTest") {
            id = "SharaSpot.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("serialization") {
            id = "SharaSpot.serialization"
            implementationClass = "SerializationConventionPlugin"
        }
        register("androidRoom") {
            id = "SharaSpot.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}
