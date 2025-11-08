import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.compose)
    alias(libs.plugins.sharaspot.koin)
    alias(libs.plugins.sharaspot.serialization)
}

android {
    namespace = "${MyProject.NAMESPACE}.feature.user"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
}