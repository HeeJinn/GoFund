package com.example.gofund.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ForgotPasswordViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (email.isEmpty()){
            onFailure("Email cannot be empty")
            return
        }
        _isLoading.value = true
        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {

                    onFailure("Failed to send password reset email: ${task.exception?.message ?: "Unknown error"}")
                }
            }
            .addOnFailureListener {
                onFailure("Failed to send password reset email: ${it.message ?: "Unknown error"}")
            }
    }
}