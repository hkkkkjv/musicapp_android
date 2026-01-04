package ru.kpfu.itis.review.impl.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.review.api.data.ReviewRepository
import ru.kpfu.itis.review.api.domain.usecases.DeleteReviewUseCase
import ru.kpfu.itis.review.api.domain.usecases.GetReviewsForSongUseCase
import ru.kpfu.itis.review.api.domain.usecases.GetUserReviewUseCase
import ru.kpfu.itis.review.impl.data.ReviewRepositoryImpl
import ru.kpfu.itis.review.impl.domain.AddReviewUseCase
import ru.kpfu.itis.review.impl.domain.DeleteReviewUseCaseImpl
import ru.kpfu.itis.review.impl.domain.GetReviewsForSongUseCaseImpl
import ru.kpfu.itis.review.impl.domain.GetUserReviewUseCaseImpl
import ru.kpfu.itis.review.impl.domain.UpdateReviewUseCase
import javax.inject.Singleton

@Module
object ReviewModule {

    @Provides
    @Singleton
    fun provideReviewRepository(
        firestore: FirebaseFirestore
    ): ReviewRepository =
        ReviewRepositoryImpl(firestore)

    @Provides
    fun provideAddReviewUseCase(
        repository: ReviewRepository,
        authRepository: AuthRepository
    ) = AddReviewUseCase(repository, authRepository)

    @Provides
    fun provideGetReviewsUseCase(
        repository: ReviewRepository
    ) = GetReviewsForSongUseCaseImpl(repository)

    @Provides
    fun provideUpdateReviewUseCase(
        repository: ReviewRepository,
        authRepository: AuthRepository
    ) = UpdateReviewUseCase(repository, authRepository)

    @Provides
    fun provideDeleteReviewUseCaseImpl(
        repository: ReviewRepository,
        authRepository: AuthRepository
    ) = DeleteReviewUseCaseImpl(repository, authRepository)

    @Provides
    fun provideGetUserReviewUseCaseImpl(
        repository: ReviewRepository,
        authRepository: AuthRepository
    ) = GetUserReviewUseCaseImpl(repository, authRepository)

    @Provides
    fun provideGetUserReviewUseCase(
        useCase: GetUserReviewUseCaseImpl
    ): GetUserReviewUseCase = useCase

    @Provides
    @Singleton
    fun provideGetReviewsForSongUseCase(
        useCase: GetReviewsForSongUseCaseImpl
    ): GetReviewsForSongUseCase = useCase

    @Provides
    @Singleton
    fun provideDeleteReviewUseCase(
        useCase: DeleteReviewUseCaseImpl
    ): DeleteReviewUseCase = useCase
}
