import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
    alias(libs.plugins.SharaSpot.compose)
    alias(libs.plugins.SharaSpot.koin)
    alias(libs.plugins.SharaSpot.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.vehicles"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
}