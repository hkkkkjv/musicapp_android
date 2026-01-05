package ru.kpfu.itis.musicapp.di

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.perf.FirebasePerformance
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(application: Application): FirebaseAnalytics {
        val analytics = Firebase.analytics
        analytics.logEvent("app_started", null)
        return analytics
    }

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        val crashlytics = Firebase.crashlytics
        crashlytics.isCrashlyticsCollectionEnabled = true
        return crashlytics
    }

    @Provides
    @Singleton
    fun provideFirebasePerformance(): FirebasePerformance {
        val performance = FirebasePerformance.getInstance()
        performance.isPerformanceCollectionEnabled = true
        return performance
    }
}