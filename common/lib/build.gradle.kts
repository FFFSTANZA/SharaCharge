import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
    alias(libs.plugins.sharaspot.serialization)
    alias(libs.plugins.sharaspot.koin)
}

android {
    namespace = "${MyProject.NAMESPACE}.common.lib"
}

dependencies {
    api(projects.common.resources)
    api(projects.core.model)
    api(projects.core.data)
    api(projects.core.network)
    api(projects.core.analytics)
    api(projects.core.database)
    api(projects.core.analytics.impl)

    //--------- core
    api(platform(libs.kotlin.bom))
    api(libs.core.ktx)
    api(libs.activity.ktx)
    api(libs.lifecycle.viewmodel.ktx)
    api(libs.work.runtime.ktx)
    api(libs.androidx.annotation)
    api(libs.kotlinx.datetime)
    implementation(libs.navigation.compose)

    gmsImplementation(libs.firebase.messaging)

    // Search places
    implementation(libs.google.places)
    implementation(libs.play.services.location)
}