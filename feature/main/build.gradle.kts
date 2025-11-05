import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
    alias(libs.plugins.SharaSpot.compose)
    alias(libs.plugins.SharaSpot.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.home"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    implementation(projects.feature.main.home)
    implementation(projects.feature.main.scan)
    implementation(projects.feature.main.orders)
    implementation(projects.feature.main.account)
}