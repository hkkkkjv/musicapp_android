package ru.kpfu.itis.impl.presentation.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.kpfu.itis.auth.api.presentation.AuthEffect
import ru.kpfu.itis.auth.api.presentation.AuthEvent
import ru.kpfu.itis.auth.api.presentation.AuthState
import ru.kpfu.itis.core.presentation.components.ErrorDialog
import ru.kpfu.itis.impl.presentation.mvi.AuthViewModel
import ru.kpfu.itis.impl.utils.phone.PhoneValidator

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit = {},
    phoneValidator: PhoneValidator = remember { PhoneValidator() }
) {
    val state by viewModel.state.collectAsState()
    val activity = LocalActivity.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AuthEffect.ShowError -> {
                    errorMessage = effect.message
                    showError = true
                }

                is AuthEffect.ShowSuccess -> {
                }

                is AuthEffect.NavigateToHome -> {
                    onNavigateToHome()
                }

                else -> {}
            }
        }
    }
    LaunchedEffect(state) {
        if (state is AuthState.Authenticated) {
            onNavigateToHome()
        }
    }
    if (errorMessage != null) {
        ErrorDialog(
            isVisible = showError,
            error = errorMessage ?: "",
            onDismiss = {
                showError = false
                errorMessage = null
                viewModel.onEvent(AuthEvent.OnErrorDismiss)
            }
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            AuthState.LoginOrRegister -> {
                LoginOrRegisterScreen(
                    onLogin = {
                        viewModel.onEvent(AuthEvent.GoToLogin)
                    },
                    onRegister = {
                        viewModel.onEvent(AuthEvent.GoToRegister)
                    }
                )
            }


            is AuthState.EnteringUsername -> {
                val nameState = state as AuthState.EnteringUsername
                UsernameInputScreen(
                    username = nameState.name,
                    onUsernameChange = { newName ->
                        viewModel.onEvent(AuthEvent.OnNameChanged(newName))
                    },
                    onSubmit = {
                        viewModel.onEvent(AuthEvent.OnNameSubmitted(nameState.name))
                    },
                    onBack = {
                        viewModel.onEvent(AuthEvent.BackFromName)
                    },
                    isLoading = nameState.isLoading
                )
            }


            is AuthState.EnteringPhone -> {
                val phoneState = state as AuthState.EnteringPhone
                PhoneInputScreen(
                    phone = phoneState.phone,
                    onPhoneChange = { newPhone ->
                        viewModel.onEvent(AuthEvent.OnPhoneChanged(newPhone))
                    },
                    onSendCode = {
                        if (activity != null) {
                            val fullPhone = "+7${phoneState.phone}"
                            viewModel.onEvent(
                                AuthEvent.OnPhoneSendCode(activity, fullPhone)
                            )
                        }
                    },
                    onBack = {
                        viewModel.onEvent(AuthEvent.BackFromPhone)
                    },
                    isLoading = phoneState.isLoading,
                    isRegistration = phoneState.isRegistration,
                    phoneValidator = phoneValidator,
                    onSignUpClick = {
                        viewModel.onEvent(AuthEvent.GoToRegister)
                    }
                )
            }

            is AuthState.EnteringCode -> {
                val codeState = state as AuthState.EnteringCode
                CodeInputScreen(
                    code = codeState.code,
                    onCodeChange = { newCode ->
                        viewModel.onEvent(AuthEvent.OnCodeChanged(newCode))
                    },
                    onVerify = {
                        viewModel.onEvent(AuthEvent.OnCodeSubmitted(codeState.code))
                    },
                    onResend = {
                        if (activity != null) {
                            viewModel.onEvent(
                                AuthEvent.OnCodeResend(activity, codeState.code)
                            )
                        }
                    },
                    onBack = {
                        viewModel.onEvent(AuthEvent.BackFromCode)
                    },
                    attemptsLeft = codeState.attemptsLeft,
                    resendCountdown = codeState.resendCountdown,
                    isLoading = codeState.isLoading
                )
            }

            AuthState.Loading -> {
                CircularProgressIndicator()
            }

            is AuthState.Error -> {
                CircularProgressIndicator()
            }

            is AuthState.Authenticated -> {
            }
        }
    }
}
