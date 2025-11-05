import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
    alias(libs.plugins.SharaSpot.koin)
}


android {
    namespace = "${MyProject.NAMESPACE}.core.analyticsImpl"
}


dependencies {
    implementation(projects.core.analytics)
}