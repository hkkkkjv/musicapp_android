package ru.kpfu.itis.profile.impl.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import ru.kpfu.itis.profile.api.data.ProfileRepository
import ru.kpfu.itis.profile.impl.data.ProfileRepositoryImpl
import ru.kpfu.itis.profile.impl.domain.GetUserProfileUseCase
import ru.kpfu.itis.profile.impl.domain.GetUserReviewsUseCase
import javax.inject.Singleton

@Module
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): ProfileRepository =
        ProfileRepositoryImpl(firestore, auth)


    @Provides
    fun provideGetUserProfileUseCase(
        repository: ProfileRepository
    ) = GetUserProfileUseCase(repository)

    @Provides
    fun provideGetUserReviewsUseCase(
        repository: ProfileRepository
    ) = GetUserReviewsUseCase(repository)
}
