package com.example.gofund.viewmodel // Or your preferred package name

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Needed for launching event emission
import com.example.gofund.model.UserData // Import your updated UserData model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.* // Use wildcard or specific imports for StateFlow, SharedFlow, etc.
import kotlinx.coroutines.launch // Needed for emitting events


// Sealed interface to represent the state of data loading
sealed interface UserDataResult {
    data object Loading : UserDataResult
    data class Success(val userData: UserData) : UserDataResult
    data class Error(val message: String) : UserDataResult
}

class HomeViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Ensure you use the correct Database URL from your Firebase project
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var userRef: DatabaseReference? = null
    private var valueEventListener: ValueEventListener? = null

    // StateFlow for UserData result (Loading, Success, Error)
    private val _userDataState = MutableStateFlow<UserDataResult>(UserDataResult.Loading)
    val userDataState: StateFlow<UserDataResult> = _userDataState.asStateFlow()

    // StateFlow for indicating if the spending limit is CURRENTLY reached/exceeded
    private val _isFundLimitReached = MutableStateFlow(false)
    val isFundLimitReached: StateFlow<Boolean> = _isFundLimitReached.asStateFlow()

    // SharedFlow for emitting a one-time event when the limit is FIRST reached
    private val _limitReachedEvent = MutableSharedFlow<Unit>(replay = 0)
    val limitReachedEvent: SharedFlow<Unit> = _limitReachedEvent.asSharedFlow()

    private val _limitSettingFeedbackEvent = MutableSharedFlow<String>()
    val limitSettingFeedbackEvent: SharedFlow<String> = _limitSettingFeedbackEvent.asSharedFlow()

    init {
        startObservingUserData()
        Log.d("HomeViewModel", "HomeViewModel Initialized")
    }

    private fun startObservingUserData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _userDataState.value = UserDataResult.Error("User not logged in.")
            if (_isFundLimitReached.value) _isFundLimitReached.value = false // Reset state
            return // Stop if no user is logged in
        }

        userRef = database.getReference("goFund").child(userId)


        if (valueEventListener == null) {
            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        val userData = snapshot.getValue(UserData::class.java)
                        if (userData != null) {
                            _userDataState.value = UserDataResult.Success(userData)
                            Log.d("HomeViewModel", "Realtime update received: $userData")
                            checkFundLimitStatus(userData) // Check status
                        } else {
                            _userDataState.value = UserDataResult.Error("Failed to parse user data.")
                            if (_isFundLimitReached.value) _isFundLimitReached.value = false // Reset state

                        }
                    } else {
                        _userDataState.value = UserDataResult.Error("User data node not found.")
                        if (_isFundLimitReached.value) _isFundLimitReached.value = false // Reset state

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                    _userDataState.value = UserDataResult.Error("Database error: ${error.message}")
                    if (_isFundLimitReached.value) _isFundLimitReached.value = false // Reset state
                    stopObservingUserData() // Clean up listener
                }
            }
            userRef?.addValueEventListener(valueEventListener!!)
            Log.d("HomeViewModel", "ValueEventListener attached successfully.")
        } else {
            Log.d("HomeViewModel", "ValueEventListener already attached.")
        }
    }

    // Function to perform the limit check based on SPENDING ALLOWANCE logic
    private fun checkFundLimitStatus(userData: UserData) {
        val limit = userData.fundLimit          // The allowed spending amount (e.g., 500)
        val balanceAtSetTime = userData.balanceWhenLimitSet // Balance when limit was set (e.g., 0)
        val currentAmount = userData.totalAmount     // Current balance (e.g., 500)

        // Check is active only if a positive limit was set AND we recorded the balance when it was set
        val isLimitActive = limit != null && limit > 0 && balanceAtSetTime != null

        var reached = false // Default to false
        if (isLimitActive && currentAmount != null) {
            // Calculate amount spent since limit was set.
            // If balance increased (current > balanceAtSetTime), spending is effectively 0 relative to the set point.
            // Use maxOf(0, ...) to ensure spending isn't negative.
            val amountSpentSinceLimitSet = maxOf(0, balanceAtSetTime!! - currentAmount!!) // Use !! because null checks done by isLimitActive

            // Limit is reached if the calculated non-negative amount spent
            // meets or exceeds the allowed spending limit.
            reached = amountSpentSinceLimitSet >= limit

            Log.d("CheckLimitStatus", "Checking Allowance: limit=$limit, setAt=$balanceAtSetTime, current=$currentAmount, spentSinceSet=$amountSpentSinceLimitSet, reached=$reached")

        } else {
            Log.d("CheckLimitStatus", "Check not active: limit=$limit, setAt=$balanceAtSetTime, current=$currentAmount")
            reached = false // Ensure reached is false if limit isn't active
        }


        // --- Emit One-Time Event on TRANSITION to reached state ---
        if (reached && !_isFundLimitReached.value) { // Check if changing from false to true
            viewModelScope.launch {
                _limitReachedEvent.emit(Unit) // Emit the event
                Log.d("HomeViewModel", ">>> Spending Allowance Limit Reached EVENT Emitted! <<<")
            }
        }
        // --------------------------------------------------------

        // Update the persistent state flow (for persistent UI warnings)
        if (_isFundLimitReached.value != reached) {
            _isFundLimitReached.value = reached
            Log.d("HomeViewModel", "Fund Limit Reached STATE Updated: $reached")
        }
        // Optional: Log even if state doesn't change, for debugging
        // else { Log.d("HomeViewModel", "Fund Limit Reached STATE remains: $reached") }
    }

    // --- MODIFIED: Function to SET/UPDATE the fund limit ---
    fun updateFundLimit(newLimit: Int) {
        val userId = auth.currentUser?.uid
        if (userId == null) { Log.e("HomeViewModel", "Cannot update limit, user null"); return }

        val currentState = _userDataState.value
        if (currentState is UserDataResult.Success) {
            val currentBalance = currentState.userData.totalAmount ?: 0

            // --- ADDED: Check if balance is positive ---
            if (currentBalance <= 0) {
                Log.w("HomeViewModel", "Attempted to set limit with non-positive balance ($currentBalance).")
                // Emit event to UI to show Toast message
                viewModelScope.launch {
                    _limitSettingFeedbackEvent.emit("You need to add funds before setting a limit.")
                }
                return // Stop execution - do not save the limit
            }
            // --- END CHECK ---

            // --- Proceed with saving if balance was positive ---
            val userRef = database.getReference("goFund").child(userId)
            val updates = mapOf<String, Any?>(
                "fundLimit" to newLimit,
                "balanceWhenLimitSet" to currentBalance // Record balance when limit is successfully set
            )

            userRef.updateChildren(updates)
                .addOnSuccessListener {
                    Log.d("HomeViewModel", "Fund limit set to $newLimit (Balance at set time: $currentBalance)")
                    // Optionally emit a SUCCESS message event
                    viewModelScope.launch {
                        _limitSettingFeedbackEvent.emit("Fund limit set to ₱$newLimit")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HomeViewModel", "Failed to update fund limit fields", e)
                    // Optionally emit an error message event
                    viewModelScope.launch {
                        _limitSettingFeedbackEvent.emit("Error setting limit: ${e.message}")
                    }
                }
            // --- End saving logic ---

        } else {
            Log.e("HomeViewModel", "Cannot set fund limit, current user data not available or not loaded.")
            viewModelScope.launch { // Emit event if data isn't loaded
                _limitSettingFeedbackEvent.emit("Cannot set limit now, please try again.")
            }
        }
    }

    // Function to RESET the fund and limit settings
    // Replace the existing resetFund function in your HomeViewModel

    fun resetFund() {
        val userId = auth.currentUser?.uid // Make sure auth is available
        if (userId == null) {
            Log.e("HomeViewModel", "Cannot reset fund, user null");
            // Optionally update state to reflect this error if needed
            _userDataState.value = UserDataResult.Error("Cannot reset fund: User not logged in.")
            return
        }
        val userRef = database.getReference("goFund").child(userId) // Make sure database is available

        // Define ALL the fields to reset and their target values
        val updates = mapOf<String, Any?>(
            // Reset counters to 0
            "numberOfExpense" to 0,
            "numberOfInvestments" to 0,
            "initialAmount" to 0, // Keep existing reset
            "totalAmount" to 0, // Keep existing reset

            // Clear list by setting to null (removes the node)
            "expenseTypeList" to null,

            // Clear limit-related fields by setting to null
            "fundLimit" to null,
            "balanceWhenLimitSet" to null
        )

        // Perform the multi-location update
        userRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "Fund reset successfully initiated. Fields reset: ${updates.keys.joinToString()}")
                // Your listener observing userRef will automatically get the updated UserData
                // with these fields zeroed or nulled, updating the UI state.
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Failed to reset fund fields", e)
                // Update state to show error
                _userDataState.value = UserDataResult.Error("Failed to reset fund: ${e.message}")
            }
    }

    // Function to remove listener safely
    private fun stopObservingUserData() {
        valueEventListener?.let { listener -> userRef?.removeEventListener(listener) }
        valueEventListener = null
        userRef = null
        Log.d("HomeViewModel", "Listener stopped.")
    }

    // Remove the listener when the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        stopObservingUserData()
        Log.d("HomeViewModel", "HomeViewModel cleared.")
    }
}