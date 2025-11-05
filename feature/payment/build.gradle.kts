import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.SharaSpot.library)
    alias(libs.plugins.SharaSpot.compose)
    alias(libs.plugins.SharaSpot.koin)
}
android {
    namespace = "${MyProject.NAMESPACE}.payment"
}

dependencies {
    implementation(projects.common.lib)
    implementation(projects.common.ui)
    // Strip payment
    implementation(libs.stripe.android) {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15to18")
    }
    implementation(libs.stripe.card.scan)
}