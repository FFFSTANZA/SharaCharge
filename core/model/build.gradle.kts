import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.model"
}

dependencies {
    implementation(libs.squareup.converter.gson)
    implementation(libs.paging.runtime.ktx)
    api(libs.kotlinx.datetime)
}