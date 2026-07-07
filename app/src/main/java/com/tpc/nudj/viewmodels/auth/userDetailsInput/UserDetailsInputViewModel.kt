package com.tpc.nudj.viewmodels.auth.userDetailsInput

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.tpc.nudj.ui.screen.auth.userDetailsInput.UserDetailsInputScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

@HiltViewModel
class UserDetailsInputViewModel @Inject constructor() : ViewModel() {
    private val _userDetailsInputUiState = MutableStateFlow(UserDetailsInputScreenUiState())
    val userDetailsInputUiState: StateFlow<UserDetailsInputScreenUiState> =
        _userDetailsInputUiState.asStateFlow()

    fun onStudentNameChange(studentName: String) {
        _userDetailsInputUiState.update { it.copy(studentName = studentName) }
    }

    fun onrollNoChange(rollNo: String) {
        _userDetailsInputUiState.update { it.copy(rollNo = rollNo) }
    }

    fun onBatchYearChange(Batch: Int) {
        _userDetailsInputUiState.update { it.copy(batchYear = Batch) }
    }

    fun onExpandedStateChange(value: Boolean) {
        _userDetailsInputUiState.update { it.copy(expanded = value) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun batchYearListFormation() {
        var startingYear = LocalDate.now().year
        val list = mutableListOf<Int>()
        for (i in 0..3) {
            list.add(startingYear - i)
        }
        if (LocalDate.now().monthValue < 8) {
            list.removeFirstOrNull()
            list.add(startingYear - 4)
        }
        _userDetailsInputUiState.update { it.copy(batchYearList = list) }
    }

    fun onSubmitButtonClick() {

    }

    fun onSkipButtonClick() {}
}
