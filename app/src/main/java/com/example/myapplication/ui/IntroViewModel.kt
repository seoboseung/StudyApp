package com.example.myapplication.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class IntroViewModel : ViewModel() {
    var id by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    val isPasswordMismatch: Boolean
        get() = confirmPassword.isNotEmpty() && password != confirmPassword

    val isSignUpEnabled: Boolean
        get() = id.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword

    fun onIdChange(newId: String) {
        id = newId
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
    }
}
