package ru.kpfu.itis.impl.presentation.mvi

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.kpfu.itis.auth.api.AuthEvent
import ru.kpfu.itis.auth.api.AuthRepository
import ru.kpfu.itis.auth.api.AuthState
import ru.kpfu.itis.core.utils.StringProvider
import ru.kpfu.itis.impl.R
import ru.kpfu.itis.impl.data.exceptions.AuthRepositoryException
import ru.kpfu.itis.impl.domain.usecase.CheckPhoneRegisteredUseCase
import ru.kpfu.itis.impl.domain.usecase.LogoutUseCase
import ru.kpfu.itis.impl.domain.usecase.RegisterUserUseCase
import ru.kpfu.itis.impl.domain.usecase.VerifyCodeUseCase
import ru.kpfu.itis.impl.utils.mapping.ErrorMessageMapper
import ru.kpfu.itis.impl.utils.phone.PhoneValidationResult
import ru.kpfu.itis.impl.utils.phone.PhoneValidator
import ru.kpfu.itis.impl.utils.runSuspendCatching
import ru.kpfu.itis.impl.utils.sms.SmsCodeValidationResult
import ru.kpfu.itis.impl.utils.sms.SmsCodeValidator
import ru.kpfu.itis.impl.utils.username.UsernameValidationResult
import ru.kpfu.itis.impl.utils.username.UsernameValidator
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

@Suppress("LongParameterList")
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val phoneValidator: PhoneValidator,
    private val smsCodeValidator: SmsCodeValidator,
    private val usernameValidator: UsernameValidator,
    private val checkPhoneRegisteredUseCase: CheckPhoneRegisteredUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val errorMessageMapper: ErrorMessageMapper,
    private val reducer: AuthReducer = AuthReducer(),
    private val stringProvider: StringProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(
        if (authRepository.isLoggedIn()) {
            AuthState.Authenticated(authRepository.getCurrentUser()!!)
        } else {
            AuthState.LoginOrRegister
        }
    )
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private var lastVerificationState: AuthState.EnteringCode? = null

    fun onEvent(event: AuthEvent) {
        val newState = reducer.reduce(_state.value, event)
        _state.value = newState

        when (event) {
            AuthEvent.GoToLogin -> {}
            AuthEvent.GoToRegister -> {}
            AuthEvent.BackFromName -> {}
            AuthEvent.BackFromPhone -> {}
            AuthEvent.BackFromCode -> {}
            is AuthEvent.OnNameChanged -> {}
            is AuthEvent.OnPhoneChanged -> {}
            is AuthEvent.OnCodeChanged -> {}
            is AuthEvent.OnNameSubmitted -> submitName(event.name)
            is AuthEvent.OnPhoneSendCode -> sendPhoneCode(event.activity, event.phone)
            is AuthEvent.OnCodeSubmitted -> submitCode(event.code)
            is AuthEvent.OnCodeResend -> resendCode(event.activity, event.phone)
            AuthEvent.OnErrorDismiss -> dismissError()
            AuthEvent.OnLogout -> logout()
        }
    }

    private fun submitName(name: String) {
        when (val validationResult = usernameValidator.validate(name)) {
            is UsernameValidationResult.Invalid -> {
                _state.update { AuthState.Error(validationResult.error) }
                return
            }

            is UsernameValidationResult.Valid -> {
            }
        }
    }

    private fun sendPhoneCode(activity: Activity, phone: String) {
        when (val validationResult = phoneValidator.validate(phone)) {
            is PhoneValidationResult.Invalid -> {
                _state.update { AuthState.Error(validationResult.error) }
                return
            }

            is PhoneValidationResult.Valid -> {
                val phoneNumber = validationResult.phoneNumber
                val currentState = _state.value
                if (currentState !is AuthState.EnteringPhone) return

                if (!currentState.isRegistration) {
                    checkPhoneAndSendSms(activity, phoneNumber, currentState)
                } else {
                    sendSms(activity, phoneNumber, currentState)
                }
            }
        }
    }

    private fun checkPhoneAndSendSms(
        activity: Activity,
        phone: String,
        currentPhoneState: AuthState.EnteringPhone
    ) {
        viewModelScope.launch {
            _state.update { currentPhoneState.copy(isLoading = true) }
            runSuspendCatching {
                checkPhoneRegisteredUseCase(phone)
            }.onSuccess { isRegistered ->
                if (!isRegistered) {
                    _state.update { currentPhoneState.copy(isLoading = false) }
                    _state.update {
                        AuthState.Error(stringProvider.getString(R.string.error_phone_not_registered))
                    }
                } else {
                    sendSmsInternal(activity, phone)
                }
            }.onFailure { error ->
                _state.update { currentPhoneState.copy(isLoading = false) }
                _state.update { AuthState.Error(getErrorMessage(error)) }
            }
        }
    }

    private fun sendSms(
        activity: Activity,
        phone: String,
        currentPhoneState: AuthState.EnteringPhone
    ) {
        viewModelScope.launch {
            _state.update { currentPhoneState.copy(isLoading = true) }
            sendSmsInternal(activity, phone)
        }
    }

    private suspend fun sendSmsInternal(activity: Activity, phone: String) {
        runSuspendCatching {
            suspendCancellableCoroutine { continuation ->
                val callbacks =
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            continuation.resumeWith((Result.success(verificationId)))
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            continuation.resumeWithException(e)
                        }
                    }

                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(callbacks)
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }.onSuccess { verificationId ->
            Log.i("AUTH-VM", "sendSms success")
            val currentState = _state.value
            Log.i("AUTH-VM", "🔍 currentState type: ${currentState::class.simpleName}")
            if (currentState is AuthState.EnteringPhone) {
                lastVerificationState = AuthState.EnteringCode(
                    verificationId = verificationId,
                    userName = currentState.userName,
                    phone = phone,
                    isRegistration = currentState.isRegistration,
                    attemptsLeft = 3,
                    resendCountdown = 30,
                    isLoading = false
                )

                _state.update { lastVerificationState!! }
                Log.i(
                    "AUTH-VM",
                    "✅ State UPDATED to EnteringCode ${lastVerificationState?.userName}"
                )
                startResendCountdown()
            } else if (currentState is AuthState.EnteringCode) {
                lastVerificationState = currentState.copy(
                    verificationId = verificationId,
                    resendCountdown = 30,
                    isLoading = false
                )
                _state.update { lastVerificationState!! }
                Log.i(
                    "AUTH-VM",
                    "✅ SMS Resent - Code State UPDATED with verificationId"
                )
                startResendCountdown()

            } else {
                Log.e(
                    "AUTH-VM",
                    "❌ ERROR: currentState is ${currentState::class.simpleName}, NOT EnteringPhone!"
                )
            }
        }.onFailure { error ->
            val currentState = _state.value
            if (currentState is AuthState.EnteringPhone) {
                _state.update { currentState.copy(isLoading = false) }
            } else if (currentState is AuthState.EnteringCode) {
                _state.update { currentState.copy(isLoading = false) }
            }
            _state.update { AuthState.Error(getErrorMessage(error)) }
        }
    }

    private fun submitCode(code: String) {
        when (val validationResult = smsCodeValidator.validate(code)) {
            is SmsCodeValidationResult.Invalid -> {
                _state.update { AuthState.Error(validationResult.error) }
                return
            }

            is SmsCodeValidationResult.Valid -> {
                val currentState = _state.value
                if (currentState !is AuthState.EnteringCode) {
                    _state.update { AuthState.Error(stringProvider.getString(R.string.error_invalid_state)) }
                    return
                }

                verifyCodeAndRegister(validationResult.code, currentState)
            }
        }
    }

    private fun verifyCodeAndRegister(code: String, codeState: AuthState.EnteringCode) {
        viewModelScope.launch {
            _state.update { codeState.copy(isLoading = true) }
            runSuspendCatching {
                verifyCodeUseCase(codeState.verificationId, code)
            }.onSuccess { user ->
                if (codeState.isRegistration && codeState.userName.isNotEmpty()) {
                    Log.i("verifyCodeAndRegister", codeState.userName)
                    runSuspendCatching {
                        Log.i("saveUserProfile", "$codeState.username$codeState.phone ")
                        registerUserUseCase(
                            codeState.userName,
                            codeState.phone
                        )
                    }.onSuccess { user ->
                        Log.i("saveUserProfile", codeState.userName)
                        _state.update { AuthState.Authenticated(user) }
                        Log.i("saveUserProfile", "Authenticated")
                    }.onFailure { error ->
                        Log.i("saveUserProfile", "Failed to save profile")
                        _state.update { codeState.copy(isLoading = false) }
                        _state.update { AuthState.Error(getErrorMessage(error)) }

                        logoutUseCase()
                    }
                } else {
                    Log.i("verifyCodeAndRegister", "✅ Login flow: authenticating user")
                    _state.update { AuthState.Authenticated(user) }
                }
            }.onFailure { error ->
                _state.update { codeState.copy(isLoading = false) }
                handleCodeError(error, codeState)
            }
        }
    }

    private fun handleCodeError(error: Throwable, codeState: AuthState.EnteringCode) {
        val attemptsLeft = codeState.attemptsLeft - 1
        if (attemptsLeft > 0) {
            val updatedState = codeState.copy(attemptsLeft = attemptsLeft)
            lastVerificationState = updatedState
            _state.update { updatedState }
            _state.update {
                AuthState.Error(
                    stringProvider.getString(
                        R.string.error_wrong_code,
                        attemptsLeft
                    )
                )
            }
        } else {
            _state.update { AuthState.Error(stringProvider.getString(R.string.error_too_many_attempts)) }
            viewModelScope.launch {
                delay(2000)
                _state.update {
                    AuthState.EnteringPhone(
                        phone = codeState.phone,
                        userName = codeState.userName,
                        isRegistration = codeState.isRegistration
                    )
                }
            }
        }
    }

    private fun resendCode(activity: Activity, phone: String) {
        val currentState = _state.value
        if (currentState !is AuthState.EnteringCode) return
        if (currentState.resendCountdown > 0) {
            _state.update {
                AuthState.Error(
                    stringProvider.getString(
                        R.string.error_wait_before_resend,
                        currentState.resendCountdown
                    )
                )
            }
            return
        }

        val phoneFromState = currentState.phone
        viewModelScope.launch {
            _state.update { currentState.copy(isLoading = true) }
            sendSmsInternal(activity, phoneFromState)
        }
    }

    private fun startResendCountdown() {
        viewModelScope.launch {
            repeat(30) {
                delay(1000)
                _state.update { currentState ->
                    if (currentState is AuthState.EnteringCode) {
                        currentState.copy(resendCountdown = 30 - (it + 1))
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { AuthState.Loading }
            runSuspendCatching {
                logoutUseCase()
            }.onSuccess {
                _state.update { AuthState.LoginOrRegister }
            }.onFailure { error ->
                _state.update { AuthState.Error(getErrorMessage(error)) }
            }
        }
    }

    private fun dismissError() {
        _state.update { currentState ->
            if (currentState is AuthState.Error) {
                lastVerificationState ?: AuthState.LoginOrRegister
            } else {
                currentState
            }
        }
    }

    private fun getErrorMessage(error: Throwable): String {
        return if (error is AuthRepositoryException) {
            val resId = errorMessageMapper.getStringResId(error.errorType)
            stringProvider.getString(resId)
        } else {
            error.message ?: stringProvider.getString(R.string.error_unknown)
        }
    }
}