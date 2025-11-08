import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.compose)
    alias(libs.plugins.sharaspot.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.feature.main.scan"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)

    //For QR code scanner
    implementation(libs.zxing.android) { isTransitive = false }
    implementation(libs.zxing.core)
}