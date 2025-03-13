package com.example.cmcandroid.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmcandroid.data.TokenManager
import com.example.cmcandroid.data.api.AuthResponse
import com.example.cmcandroid.data.repository.CmcRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: CmcRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val response = repository.login(username, password)
                tokenManager.saveToken(response.token)
                _authState.value = AuthState.Success(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val response = repository.register(username, email, password)
                tokenManager.saveToken(response.token)
                _authState.value = AuthState.Success(response)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    data class Success(val response: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
} 