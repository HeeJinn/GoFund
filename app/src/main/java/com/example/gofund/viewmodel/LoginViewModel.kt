package com.example.gofund.viewmodel

import android.app.Application // Use Application context
import android.util.Log
import androidx.lifecycle.AndroidViewModel // Inherit from AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gofund.model.UserData // Keep your UserData model import
import com.example.gofund.userpreference.UserPreferences
import com.example.gofund.userpreference.UserPreferencesRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Change ViewModel to AndroidViewModel to get Application context
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth

    // Instantiate the repository (Or use Hilt/Koin for Dependency Injection)
    private val userPrefsRepository = UserPreferencesRepository(application)

    // Expose the user preferences as StateFlow
    val userPreferences: StateFlow<UserPreferences> = userPrefsRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            // Keep flow active for 5s after last collector stops, helps survive config changes briefly
            started = SharingStarted.WhileSubscribed(5000L),
            // Default initial value before DataStore loads
            initialValue = UserPreferences("", "", false)
        )

    // Modified login function to accept rememberMe and save prefs
    fun login(
        email: String,
        password: String,
        rememberMe: Boolean, // Added this parameter
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Login successful & email verified
                        viewModelScope.launch {
                            // Update DataStore based on rememberMe state
                            userPrefsRepository.updateLoginCredentials(email, password, rememberMe)
                        }
                        onSuccess()
                    } else {
                        // Handle case where email is not verified
                        auth.signOut() // Sign out if email not verified
                        onFailure("Please verify your email before logging in.")
                    }
                } else {
                    // Login failed
                    onFailure("Login failed: ${task.exception?.message ?: "Unknown error"}")
                }
            }
    }

    // --- Your existing signUp and createUserData functions ---
    // Keep them exactly as they were in your original code.
    // Note the security warning about storing passwords in createUserData applies here too.
    fun signUp(email: String, userName: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { user ->
                        user.sendEmailVerification()
                            .addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    Log.d("sendEmail", "Email verification sent")
                                    createUserData(email, password, userName, 0, 0) { isSuccess, errorMessage ->
                                        if(isSuccess){
                                            onSuccess()
                                        } else {
                                            onFailure(errorMessage ?: "Failed to create user data")
                                        }
                                    }
                                } else {
                                    onFailure("Failed to send verification email: ${verificationTask.exception?.message ?: "Unknown error"}")
                                }
                            }
                    } ?: onFailure("User creation failed.")
                } else {
                    onFailure("Sign-up failed: ${task.exception?.message ?: "Unknown error"}")
                }
            }
    }

    private fun createUserData(email: String, password: String, userName: String, totalAmount: Int = 0, initialAmount: Int = 0, onComplete: (Boolean, String?) -> Unit){ //Added onComplete
        val firebase = Firebase.database("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val dbRef = firebase.getReference("goFund")
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        if (userID != null) {
            // *** SECURITY WARNING ***: Storing plain password in Realtime Database is insecure!
            val userData = UserData(email = email, password = password, userName = userName, totalAmount = totalAmount, initialAmount = initialAmount)
            dbRef.child(userID).setValue(userData)
                .addOnSuccessListener {
                    Log.d("USER_DATA", "user created")
                    onComplete(true, null)
                }
                .addOnFailureListener { e ->
                    Log.e("USER_DATA", "Error creating user data: ${e.message}")
                    onComplete(false, e.message)
                }
        } else {
            Log.e("USER_DATA", "User ID is null. Cannot create user data.")
            onComplete(false, "User ID is null")
        }
    }
}