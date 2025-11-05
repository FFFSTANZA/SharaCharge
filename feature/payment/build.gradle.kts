import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
    alias(libs.plugins.SharaSpot.compose)
    alias(libs.plugins.SharaSpot.koin)
}
android {
    namespace = "${MyProject.NAMESPACE}.payment"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
}