import java.util.Properties
import java.io.FileInputStream
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val apiUrl: String = localProperties.getProperty("API_URL")
    ?: "APIURL_NO_DEFINIDO"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.nramos.ruletasolsecurity"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nramos.ruletasolsecurity"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "API_URL", "\"$apiUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.androidx.cardview)

    // Retrofit para llamadas a API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines para manejar hilos
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // Para viewModelScope
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    // Para LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
}