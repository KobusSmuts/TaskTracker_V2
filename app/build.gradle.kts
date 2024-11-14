plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Firebase services
}

android {
    namespace = "com.example.tasktracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tasktracker"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Core dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    // Firebase dependencies for Authentication and Database
    implementation(platform("com.google.firebase:firebase-bom:33.5.1")) // Firebase BOM for version alignment
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    // Room for offline database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    annotationProcessor("androidx.room:room-compiler:2.6.1") // Use 'kapt' if you're using Kotlin

    // Lifecycle dependencies for ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
