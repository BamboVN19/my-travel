plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.travel.mytravel"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.travel.mytravel"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Các thư viện API
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.glide)
    implementation(libs.circleimageview)
    implementation(libs.retrofit.mock)

    // Unit Test
    testImplementation(libs.junit)

    // Android Test
    // Sửa libs.ext.junit thành libs.androidx.junit
    androidTestImplementation(libs.androidx.junit)
    // Sửa libs.espresso.core thành libs.androidx.espresso.core
    androidTestImplementation(libs.androidx.espresso.core)
}