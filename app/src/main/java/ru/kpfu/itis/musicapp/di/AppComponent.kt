package ru.kpfu.itis.musicapp.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import dagger.BindsInstance
import dagger.Component
import ru.kpfu.itis.core.di.CoreModule
import ru.kpfu.itis.impl.di.AuthModule
import ru.kpfu.itis.musicapp.MainActivity
import ru.kpfu.itis.song.impl.di.SongModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreModule::class,
        AppModule::class,
        AuthModule::class,
        SongModule::class,
        FirebaseModule::class,
        ViewModelModule::class,
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
    val viewModelFactory: ViewModelProvider.Factory
}