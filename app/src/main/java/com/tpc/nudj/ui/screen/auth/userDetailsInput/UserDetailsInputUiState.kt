package com.tpc.nudj.ui.screen.auth.userDetailsInput


data class UserDetailsInputScreenUiState(
    val studentName: String = "",
    val rollNo: String = "",
    val batchYear: Int? = null,
    val expanded: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String? = null,
    val batchYearList: MutableList<Int> = mutableListOf(),
)




