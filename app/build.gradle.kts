import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// 从 local.properties 读取签名信息（该文件已被 .gitignore 忽略，不会上传到 GitHub）
val localProps = Properties().apply {
    val propsFile = rootProject.file("local.properties")
    if (propsFile.exists()) {
        propsFile.inputStream().use { this.load(it) }
    }
}

android {
    namespace = "com.example.multiweather"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }
    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.multiweather"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("config"){
            storeFile = file(localProps.getProperty("KEY_PATH"))
            storePassword = localProps.getProperty("KEY_PASS")
            keyAlias = localProps.getProperty("ALIAS_NAME")
            keyPassword = localProps.getProperty("ALIAS_PASS")
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            signingConfig = signingConfigs.getByName("config")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}