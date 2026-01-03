package ru.kpfu.itis.musicapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.kpfu.itis.impl.presentation.mvi.AuthViewModel
import ru.kpfu.itis.musicapp.AuthStateViewModel
import ru.kpfu.itis.song.impl.presentation.SearchViewModel
import ru.kpfu.itis.song.impl.presentation.details.SongDetailsViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(viewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthStateViewModel::class)
    abstract fun bindAuthStateViewModel(viewModel: AuthStateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SongDetailsViewModel::class)
    abstract fun bindSongDetailsViewModel(viewModel: SongDetailsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}