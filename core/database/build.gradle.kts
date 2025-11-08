import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.room)
    alias(libs.plugins.sharaspot.koin)
    alias(libs.plugins.sharaspot.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.database"
}

dependencies {
    implementation(projects.core.model)
}