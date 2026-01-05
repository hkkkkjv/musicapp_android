import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.gradle.secrets)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
}
android {
    namespace = "ru.kpfu.itis.musicapp"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ru.kpfu.itis.musicapp"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = rootProject.extra.get("versionCode") as Int
        versionName = rootProject.extra.get("versionName") as String


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += listOf(
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
                "META-INF/versions/*/OSGI-INF/MANIFEST.MF",
                "META-INF/MANIFEST.MF",
                "META-INF/*.properties",
                "META-INF/proguard/*.pro",
                "META-INF/versions/*/module-info.class",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/licenses/**"
            )
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = project.findProperty("storePassword")?.toString() ?: ""
            keyAlias = project.findProperty("keyAlias")?.toString() ?: ""
            keyPassword = project.findProperty("keyPassword")?.toString() ?: ""
        }
    }

    buildTypes {
        release {
            //isMinifyEnabled = true
            //isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
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
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            allWarningsAsErrors.set(false)
            freeCompilerArgs.addAll(
                "-Xopt-in=kotlin.RequiresOptIn"
            )
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core)
    implementation(projects.feature.auth.api)
    implementation(projects.feature.auth.impl)
    implementation(projects.feature.song.api)
    implementation(projects.feature.song.impl)
    implementation(projects.feature.review.api)
    implementation(projects.feature.review.impl)
    implementation(projects.feature.profile.api)
    implementation(projects.feature.profile.impl)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Retrofit2
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor.v530)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.activity.compose)

    // Navigation
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    // Kotlin
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    // DI - Dagger
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    // Coil
    implementation(libs.coil)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)


    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    androidTestImplementation(libs.androidx.test.runner)

    debugImplementation(libs.androidx.compose.ui.tooling)
    detektPlugins(libs.detekt.formatting)


    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.ui.compose)
}

detekt {
    config.setFrom("${rootProject.projectDir}/detekt.yml")
    autoCorrect = false

    reports {
        html.required.set(true)
        txt.required.set(false)
        xml.required.set(false)
        sarif.required.set(false)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}
tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
