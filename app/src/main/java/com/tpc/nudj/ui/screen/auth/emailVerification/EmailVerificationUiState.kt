package com.tpc.nudj.ui.screen.auth.emailVerification

data class EmailVerificationUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val timerInSeconds: Int = 30,
    val isResendEnabled: Boolean = true
)
