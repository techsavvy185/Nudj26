package com.tpc.nudj.viewmodels.auth.emailVerification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.tpc.nudj.ui.screen.auth.emailVerification.EmailVerificationUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EmailVerificationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()
    fun onResendEmailClick() {
        startTimer()
    }



    private var timerJob: Job? = null

    fun startTimer(){
        timerJob?.cancel()
        timerJob = viewModelScope.launch {

            _uiState.update {
                it.copy(isResendEnabled = false, timerInSeconds = 30)
            }
            for(i in 29 downTo 0){
                delay(1000)
                _uiState.update { it.copy(timerInSeconds = i) }
            }
            _uiState.update {
                it.copy(isResendEnabled = true)
            }
        }

    }
}