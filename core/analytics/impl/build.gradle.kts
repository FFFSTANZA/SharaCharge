import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.koin)
}


android {
    namespace = "${MyProject.NAMESPACE}.core.analyticsImpl"
}


dependencies {
    implementation(projects.core.analytics)
}