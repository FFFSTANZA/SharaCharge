import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
}

dependencies {
    implementation(libs.material)
}

android {
    namespace = "${MyProject.NAMESPACE}.resources"
}
