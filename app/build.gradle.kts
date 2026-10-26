plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.rohit.submeter2"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.rohit.submeter2"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true // or dataBinding = true
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)


    implementation("net.sourceforge.jexcelapi:jxl:2.6.12")

    // Lightweight, open-source PDF generator for tables and documents
    implementation("com.itextpdf:itextg:5.5.10")

    val cameraxVersion = "1.3.0"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    implementation("com.google.android.material:material:1.14.0")

    // dependency for slider view
//    implementation("com.github.smarteist:Android-Image-Slider:1.4.0")
//    implementation("com.github.smarteist:autoimageslider:1.4.0")
// Usually paired with Glide or Picasso to load the images
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation("com.github.smarteist:Android-Image-Slider:1.4.0")


    implementation(project(":rrAllUtilityLib"))
    implementation(project(":BaseLibrary"))
    implementation(project(":android_ui_templete1"))
    implementation(project(":submeterServiceLib"))
    implementation(project(":submeterCommonLib"))
    implementation(project(":submeterPresentationLib"))


}