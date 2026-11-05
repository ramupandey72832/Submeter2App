plugins {
    id("com.android.library")
}


android {
    // Add your module's namespace package name here:
    namespace = "com.submeter.presentation"

    compileSdk = 36 // Use '=' assignment in Kotlin DSL

    defaultConfig {
        minSdk = 24
       // targetSdk is not required for library modules
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


dependencies {
    implementation(project(":rrAllUtilityLib"))
    implementation(project(":submeterCommonLib"))
    implementation(project(":submeterServiceLib"))
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.0")

    // Lombok for main code
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")

    testCompileOnly("org.projectlombok:lombok:1.18.48")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.48")



}