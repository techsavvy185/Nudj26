package com.tpc.nudj.viewmodels.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tpc.nudj.model.AuthResult
import com.tpc.nudj.model.enums.Role
import com.tpc.nudj.repository.auth.AuthRepository
import com.tpc.nudj.repository.auth.GoogleSignInClient
import com.tpc.nudj.repository.user.UserRepository
import com.tpc.nudj.ui.screen.auth.login.LoginUiState
import com.tpc.nudj.utils.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun onEmailChange(email: String) {
        _loginUiState.update {
            it.copy(email = email)
        }
    }

    fun onPasswordChange(password: String) {
        _loginUiState.update {
            it.copy(password = password)
        }


    }
    fun togglePasswordVisibility() {
        _loginUiState.update {
            it.copy(
                passwordVisible = !it.passwordVisible
            )
        }
    }
    fun onLoginClick() {
        Validator.isValidEmail(_loginUiState.value.email).fold(
            {performFirebaseLogin()},
            {error -> displayErrorMessage(error.message ?: "Invalid email")}
        )
    }
    fun performFirebaseLogin(){
        if(_loginUiState.value.password.isBlank()){
            displayErrorMessage("Password can't be empty")
            return
        }
        viewModelScope.launch {
            val email = _loginUiState.value.email
            val password = _loginUiState.value.password
            authRepository.signInWithEmailAndPassword(email,password).collect {status ->
                when(status){
                    AuthResult.Loading -> {
                        _loginUiState.update { it.copy(isLoading = true) }
                    }
                    AuthResult.Initial -> {}
                    is AuthResult.VerificationNeeded -> {
                        _loginUiState.update { it.copy(isLoading = false) }
                    }
                    is AuthResult.Success -> {
                        validateRole(status.user.role)
                    }
                    is AuthResult.Error -> {
                        _loginUiState.update { it.copy(isLoading = false) }
                        displayErrorMessage(status.message)
                    }
                }
            }

        }
    }
    fun displayErrorMessage(errorMessage: String){
        _loginUiState.update{
            it.copy(errorMessage=errorMessage)
        }
    }
    fun clearError(){
        _loginUiState.update{
            it.copy(
                errorMessage = null
            )
        }
    }

    fun onForgotPasswordClick() {}

    fun onGoogleClick(context: Context) {
        viewModelScope.launch {
            val idToken = googleSignInClient.signIn(context)
            if (idToken == null) {
                displayErrorMessage("Google Sign-In failed.")
                return@launch
            }
            authRepository
                .signInWithGoogle(idToken, loginUiState.value.role)
                .collect { status ->
                    when (status) {
                        AuthResult.Loading -> {
                            _loginUiState.update {
                                it.copy(isLoading = true)
                            }
                        }
                        is AuthResult.Success -> {
                            val firebaseUser = status.user
                            if (!userRepository.userExists(firebaseUser.uid)) {
                                authRepository.signOut()
                                _loginUiState.update {
                                    it.copy(isLoading = false)
                                }
                                displayErrorMessage(
                                    "Account doesn't exist. Please register first."
                                )
                                return@collect
                            }
                            val firestoreRole =
                                userRepository.fetchUserRole(firebaseUser.uid)
                            validateRole(firestoreRole)
                        }
                        is AuthResult.Error -> {
                            _loginUiState.update {
                                it.copy(isLoading = false)
                            }
                            displayErrorMessage(status.message)
                        }
                        AuthResult.Initial,
                        is AuthResult.VerificationNeeded -> {
                            _loginUiState.update {
                                it.copy(isLoading = false)
                            }
                        }
                    }
                }
        }
    }

    fun onRoleSelected(role: Role) {
        _loginUiState.update {
            it.copy(role = role)
        }
    }

    private suspend fun validateRole(registeredRole : Role){
        if(registeredRole != _loginUiState.value.role){
            authRepository.signOut()
            _loginUiState.update {
                it.copy(isLoading = false)
            }
            displayErrorMessage("Unauthorized. Please use the correct Portal layout role to login.")
            return
        }
        _loginUiState.update {
            it.copy(isLoading = false)
        }
        return
    }

}