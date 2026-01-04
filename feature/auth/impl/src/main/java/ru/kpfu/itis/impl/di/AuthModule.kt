package ru.kpfu.itis.impl.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.auth.api.domain.usecase.GetCurrentUserIdUseCase
import ru.kpfu.itis.core.utils.StringProvider
import ru.kpfu.itis.impl.data.AuthRepositoryImpl
import ru.kpfu.itis.impl.domain.usecase.CheckPhoneRegisteredUseCase
import ru.kpfu.itis.impl.domain.usecase.GetCurrentUserIdUseCaseImpl
import ru.kpfu.itis.impl.domain.usecase.LogoutUseCase
import ru.kpfu.itis.impl.domain.usecase.RegisterUserUseCase
import ru.kpfu.itis.impl.domain.usecase.VerifyCodeUseCase
import ru.kpfu.itis.impl.presentation.mvi.AuthReducer
import ru.kpfu.itis.impl.utils.mapping.ErrorMessageMapper
import ru.kpfu.itis.impl.utils.phone.PhoneValidator
import ru.kpfu.itis.impl.utils.sms.SmsCodeValidator
import ru.kpfu.itis.impl.utils.username.UsernameValidator
import javax.inject.Singleton

@Module
class AuthModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth, firestore)

    @Provides
    @Singleton
    fun providePhoneValidator(): PhoneValidator =
        PhoneValidator()


    @Provides
    @Singleton
    fun provideSmsCodeValidator(): SmsCodeValidator =
        SmsCodeValidator()


    @Provides
    @Singleton
    fun provideUsernameValidator(): UsernameValidator =
        UsernameValidator()


    @Provides
    @Singleton
    fun provideCheckPhoneRegisteredUseCase(
        authRepository: AuthRepository
    ): CheckPhoneRegisteredUseCase =
        CheckPhoneRegisteredUseCase(authRepository)


    @Provides
    @Singleton
    fun provideVerifyCodeUseCase(
        authRepository: AuthRepository
    ): VerifyCodeUseCase =
        VerifyCodeUseCase(authRepository)


    @Provides
    @Singleton
    fun provideRegisterUserUseCase(
        authRepository: AuthRepository
    ): RegisterUserUseCase =
        RegisterUserUseCase(authRepository)


    @Provides
    @Singleton
    fun provideLogoutUseCase(
        authRepository: AuthRepository
    ): LogoutUseCase =
        LogoutUseCase(authRepository)


    @Provides
    @Singleton
    fun provideErrorMessageMapper(
        stringProvider: StringProvider
    ): ErrorMessageMapper =
        ErrorMessageMapper()


    @Provides
    @Singleton
    fun provideAuthReducer(): AuthReducer =
        AuthReducer()

    @Provides
    @Singleton
    fun bindGetSongDetailsUseCase(
        useCase: GetCurrentUserIdUseCaseImpl
    ): GetCurrentUserIdUseCase = useCase
}
