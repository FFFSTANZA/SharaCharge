import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
    alias(libs.plugins.SharaSpot.room)
    alias(libs.plugins.SharaSpot.koin)
    alias(libs.plugins.SharaSpot.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.database"
}

dependencies {
    implementation(projects.core.model)
}