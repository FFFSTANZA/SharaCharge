import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
    alias(libs.plugins.SharaSpot.compose)
    alias(libs.plugins.SharaSpot.hilt)
}

android {
    namespace = "${MyProject.NAMESPACE}.charging"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
}