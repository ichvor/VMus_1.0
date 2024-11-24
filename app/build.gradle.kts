plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Для Room
}

android {
    namespace = "com.example.mediaplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mediaplayer"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true // Включаем View Binding
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

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Основные библиотеки Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ViewModel и LiveData (архитектура Android)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Room (база данных)
    implementation("androidx.room:room-runtime:2.6.0")
    implementation(libs.espresso.core)
    kapt("androidx.room:room-compiler:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Media (для работы с аудио/видео)
    implementation("androidx.media:media:1.6.0")

    // Фрагменты
    implementation("androidx.fragment:fragment-ktx:1.6.0")

    // Для viewModels в Activity
    implementation("androidx.activity:activity-ktx:1.7.0")

    // Тестирование
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.media:media:1.6.0")
    implementation ("androidx.core:core-ktx:1.10.1")
}
