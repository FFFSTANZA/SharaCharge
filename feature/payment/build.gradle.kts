import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.compose)
    alias(libs.plugins.sharaspot.koin)
}
android {
    namespace = "${MyProject.NAMESPACE}.payment"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)

    // Razorpay Payment Gateway
    implementation("com.razorpay:checkout:1.6.40")
}