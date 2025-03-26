package com.example.gofund.viewmodel // Or your preferred package

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gofund.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Import await()

// Define a UI state holder data class
data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isUserLoggedIn: Boolean = true, // Assume logged in initially
    val userName: String = "Loading...",
    val totalAmount: Int = 0,
    val initialAmount: Int = 0
)

class HomeViewModel : ViewModel() {

    private val TAG = "HOME_VIEWMODEL_PROCESS" // Specific TAG for ViewModel

    // Firebase references (initialized once)
    private val auth: FirebaseAuth = Firebase.auth
    // Consider injecting these or using a Singleton pattern for better testability
    private val database = Firebase.database("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var dbRef: DatabaseReference? = null // Reference specific to user

    // UI State Flow
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized.")
        observeAuthState() // Start observing auth state
        // Initial fetch based on current user (if any)
        fetchUserData(auth.currentUser?.uid)
    }

    // Optional: Observe auth state changes if needed for real-time updates on login/logout
    private fun observeAuthState() {
        // If you need the screen to react instantly to login/logout without restarting the app,
        // you'd add an AuthStateListener here and call fetchUserData accordingly.
        // For simplicity, we'll rely on the initial fetch and potential recomposition/ViewModel recreation.
        Log.d(TAG, "Auth state observation setup (if implemented).")
    }

    private fun fetchUserData(userId: String?) {
        Log.d(TAG, "Attempting to fetch user data for UserID: $userId")
        if (userId == null) {
            Log.w(TAG, "User ID is null. Setting logged out state.")
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isUserLoggedIn = false,
                    userName = "Not signed in",
                    errorMessage = null // Clear previous errors
                )
            }
            return
        }

        // Set user-specific database reference
        dbRef = database.getReference("goFund").child(userId)

        // Launch coroutine to fetch data
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) } // Set loading state
            try {
                Log.d(TAG, "Calling Firebase get().await() for user data.")
                val snapshot = dbRef!!.get().await() // Use await() for cleaner async code

                Log.d(TAG, "Firebase get() completed. Snapshot exists: ${snapshot.exists()}")
                if (snapshot.exists()) {
                    try {
                        Log.d(TAG, "Attempting to parse UserData...")
                        val userData = snapshot.getValue(UserData::class.java)

                        if (userData != null) {
                            Log.d(TAG, "Successfully parsed UserData.")
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isUserLoggedIn = true,
                                    userName = userData.userName ?: "User",
                                    totalAmount = userData.totalAmount ?: 0,
                                    initialAmount = userData.initialAmount ?: 0,
                                    errorMessage = null
                                )
                            }
                        } else {
                            // Should ideally not happen if snapshot exists but parsing returns null
                            Log.e(TAG,"UserData parsed as null despite snapshot existing.")
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isUserLoggedIn = true, // Still logged in
                                    userName = "Error: Invalid data",
                                    errorMessage = "Could not read user data structure."
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "!!!!!! FAILED TO PARSE UserData !!!!!!", e)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isUserLoggedIn = true, // Still logged in
                                userName = "Error: Parse failed",
                                errorMessage = "Error parsing user data: ${e.message}"
                            )
                        }
                    }
                } else {
                    Log.w(TAG, "User node does not exist for ID: $userId.")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isUserLoggedIn = false, // Treat non-existent user as logged out/error
                            userName = "User not found",
                            errorMessage = "User data not found."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firebase get() failed with exception.", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isUserLoggedIn = true, // We assume still logged in, just failed to get data
                        userName = "Error",
                        errorMessage = "Failed to fetch data: ${e.message}"
                    )
                }
            }
        }
    }

    // Optional: Function to manually refresh data
    fun refreshData() {
        fetchUserData(auth.currentUser?.uid)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "HomeViewModel onCleared.")
        // No listeners to remove in this version
    }
}