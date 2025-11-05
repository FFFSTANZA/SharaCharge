import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
    alias(libs.plugins.SharaSpot.compose)
    alias(libs.plugins.SharaSpot.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.account"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    //QR Generator
    implementation(libs.custom.qr.generator)
}