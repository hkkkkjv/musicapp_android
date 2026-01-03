package ru.kpfu.itis.impl.presentation.mvi

import ru.kpfu.itis.auth.api.AuthEvent
import ru.kpfu.itis.auth.api.AuthState
import javax.inject.Inject

class AuthReducer @Inject constructor() {

    fun reduce(state: AuthState, event: AuthEvent): AuthState {
        return when (event) {
            AuthEvent.GoToLogin -> navigateToLogin()
            AuthEvent.GoToRegister -> navigateToRegister()
            AuthEvent.BackFromName -> backFromName()
            AuthEvent.BackFromPhone -> backFromPhone(state)
            AuthEvent.BackFromCode -> backFromCode(state)

            is AuthEvent.OnNameChanged -> handleNameChanged(state, event)
            is AuthEvent.OnPhoneChanged -> handlePhoneChanged(state, event)
            is AuthEvent.OnCodeChanged -> handleCodeChanged(state, event)

            is AuthEvent.OnNameSubmitted -> submitName(event)
            is AuthEvent.OnPhoneSendCode -> state
            is AuthEvent.OnCodeSubmitted -> state
            is AuthEvent.OnCodeResend -> state

            AuthEvent.OnErrorDismiss -> state
            AuthEvent.OnLogout -> state
        }
    }

    private fun navigateToLogin(): AuthState =
        AuthState.EnteringPhone(isRegistration = false)


    private fun navigateToRegister(): AuthState =
        AuthState.EnteringUsername()


    private fun backFromName(): AuthState =
        AuthState.LoginOrRegister


    private fun backFromPhone(state: AuthState): AuthState {
        return if (state is AuthState.EnteringPhone && state.isRegistration) {
            AuthState.EnteringUsername(name = state.userName)
        } else {
            AuthState.LoginOrRegister
        }
    }

    private fun backFromCode(state: AuthState): AuthState {
        return if (state is AuthState.EnteringCode) {
            AuthState.EnteringPhone(
                phone = state.phone,
                userName = state.userName,
                isRegistration = state.isRegistration
            )
        } else {
            state
        }
    }

    private fun handleNameChanged(
        state: AuthState,
        event: AuthEvent.OnNameChanged
    ): AuthState {
        return if (state is AuthState.EnteringUsername) {
            state.copy(name = event.name)
        } else {
            state
        }
    }

    private fun handlePhoneChanged(
        state: AuthState,
        event: AuthEvent.OnPhoneChanged
    ): AuthState {
        return if (state is AuthState.EnteringPhone) {
            state.copy(phone = event.phone)
        } else {
            state
        }
    }

    private fun handleCodeChanged(
        state: AuthState,
        event: AuthEvent.OnCodeChanged
    ): AuthState {
        return if (state is AuthState.EnteringCode) {
            state.copy(code = event.code)
        } else {
            state
        }
    }

    private fun submitName(event: AuthEvent.OnNameSubmitted): AuthState {
        return AuthState.EnteringPhone(
            userName = event.name,
            isRegistration = true
        )
    }
}
