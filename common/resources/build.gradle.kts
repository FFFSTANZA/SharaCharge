import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
}

dependencies {
    implementation(libs.material)
}

android {
    namespace = "${MyProject.NAMESPACE}.resources"
}
