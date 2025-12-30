package ru.kpfu.itis.impl.presentation.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import ru.kpfu.itis.auth.api.AuthRepository
import ru.kpfu.itis.impl.data.AuthRepositoryImpl
import javax.inject.Singleton

@Module
class AuthModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth, firestore)
}