package ru.kpfu.itis.musicapp

import android.app.Application
import ru.kpfu.itis.musicapp.di.DaggerAppComponent

class MusicApp : Application() {

    val appComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
    }
}