package ru.kpfu.itis.musicapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.kpfu.itis.auth.api.AuthRepository
import ru.kpfu.itis.auth.api.AuthState
import javax.inject.Inject

class AuthStateViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(
        if (authRepository.isLoggedIn()) {
            AuthState.Authenticated(authRepository.getCurrentUser()!!)
        } else {
            AuthState.LoginOrRegister
        }
    )

    val authState: StateFlow<AuthState> = _authState
}