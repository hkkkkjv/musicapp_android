package ru.kpfu.itis.musicapp.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.kpfu.itis.core.di.CoreModule
import ru.kpfu.itis.impl.presentation.di.AuthModule
import ru.kpfu.itis.musicapp.MainActivity
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreModule::class,
        AuthModule::class,
        FirebaseModule::class
    ]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
    fun inject(activity: MainActivity)
}