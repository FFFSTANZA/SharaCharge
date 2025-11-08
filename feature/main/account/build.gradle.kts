import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.compose)
    alias(libs.plugins.sharaspot.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.feature.main.account"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    //QR Generator
    implementation(libs.custom.qr.generator)
}