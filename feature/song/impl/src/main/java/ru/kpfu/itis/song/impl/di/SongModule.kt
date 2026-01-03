package ru.kpfu.itis.song.impl.di

import dagger.Binds
import dagger.Module
import ru.kpfu.itis.song.api.SearchRepository
import ru.kpfu.itis.song.impl.data.remote.SearchRepositoryImpl

@Module
abstract class SongModule {

    @Binds
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
}