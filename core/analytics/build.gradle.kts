import com.SharaSpot.MyProject

plugins {
    alias(libs.plugins.sharaspot.library)
}


android {
    namespace = "${MyProject.NAMESPACE}.core.analytics"
}
