import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.compose)
    alias(libs.plugins.sharaspot.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.feature.charging"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    implementation(projects.core.model)
}