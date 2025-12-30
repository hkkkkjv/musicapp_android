package ru.kpfu.itis.impl.presentation.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.kpfu.itis.auth.api.AuthEvent
import ru.kpfu.itis.auth.api.AuthState
import ru.kpfu.itis.core.presentation.components.ErrorDialog
import ru.kpfu.itis.impl.R
import ru.kpfu.itis.impl.presentation.mvi.AuthViewModel
import ru.kpfu.itis.impl.utils.phone.PhoneValidator
import ru.kpfu.itis.impl.utils.phone.PhoneVisualTransformation

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit = {},
    phoneValidator: PhoneValidator = remember { PhoneValidator() }
) {
    val state by viewModel.state.collectAsState()
    val activity = LocalActivity.current

    LaunchedEffect(state) {
        if (state is AuthState.Authenticated) {
            onNavigateToHome()
        }
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
                val errorState = state as AuthState.Error
                ErrorDialog(
                    error = errorState.message,
                    onDismiss = {
                        viewModel.onEvent(AuthEvent.OnErrorDismiss)
                    }
                )
            }

            is AuthState.Authenticated -> {
            }
        }
    }
}
