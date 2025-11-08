import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
}

android {
    namespace = "${MyProject.NAMESPACE}.core.model"

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
        }
    }
}

dependencies {
    implementation(libs.squareup.converter.gson)
    implementation(libs.paging.runtime.ktx)
    api(libs.kotlinx.datetime)
}