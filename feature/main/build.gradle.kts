import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.compose)
    alias(libs.plugins.sharaspot.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.feature.main"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    implementation(projects.feature.main.home)
    implementation(projects.feature.main.scan)
    implementation(projects.feature.main.orders)
    implementation(projects.feature.main.account)
}