import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.compose)
    alias(libs.plugins.sharaspot.hilt)
}

android {
    namespace = "${MyProject.NAMESPACE}.charging"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
}