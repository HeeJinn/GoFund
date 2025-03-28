package com.example.gofund.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BSheetViewModel: ViewModel() {

    private val firebase = Firebase.database("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private val _openBottomSheet = MutableStateFlow(false)
    val openBottomSheet : StateFlow<Boolean> = _openBottomSheet.asStateFlow()

    private val _userDataState = MutableStateFlow<UserDataResult>(UserDataResult.Loading)
    val userDataState: StateFlow<UserDataResult> = _userDataState.asStateFlow()

    fun openSheet(){
        _openBottomSheet.value = true
    }
    fun closeSheet(){
        _openBottomSheet.value = false
    }
    // Add this function inside your HomeViewModel class
    fun updateFundLimit(newLimit: Int) {
        val userID = Firebase.auth.currentUser?.uid
        val userRef = firebase.getReference("goFund").child(userID!!)
        if (userID == null) {
            Log.e("HomeViewModel", "Cannot update limit, user not logged in.")
            // Optionally update state to show an error
            // _userDataState.value = UserDataResult.Error("User not logged in to set limit.")
            return
        }
        // Ensure database reference setup is correct
        val userLimitRef = userRef.child("fundLimit") // Reference the specific field

        userLimitRef.setValue(newLimit)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "Successfully updated fund limit to $newLimit")
                // Optional: You could emit a temporary success message state if needed,
                // but the listener will update the main state anyway.
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Failed to update fund limit", e)
                // Optionally update state to show an error
                // _userDataState.value = UserDataResult.Error("Failed to set limit: ${e.message}")
            }
    }
    fun resetFund() {
        val userID = Firebase.auth.currentUser?.uid // Make sure 'auth' is accessible in HomeViewModel
        if (userID == null) {
            Log.e("HomeViewModel", "Cannot reset fund, user not logged in.")
            _userDataState.value = UserDataResult.Error("User not logged in to reset fund.") // Update state
            return
        }
        // Make sure 'database' ref is accessible in HomeViewModel
        val userRef = firebase.getReference("goFund").child(userID)

        // Define the fields to reset and their default values
        val updates = mapOf<String, Any?>(
            "totalAmount" to 0,
            "initialAmount" to 0, // Set total amount back to 0
            "fundLimit" to null // Set fund limit to null (meaning no limit), or 0 if that's your default
            // Add "initialAmount" to 0 here if that should also be reset
            // "initialAmount" to 0
        )

        // Update the specific children in Firebase
        userRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "Fund reset successfully for user $userID.")
                // The ValueEventListener will automatically pick up this change
                // and update the _userDataState, flowing to the UI.
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Failed to reset fund", e)
                _userDataState.value = UserDataResult.Error("Failed to reset fund: ${e.message}") // Update state
            }
    }

}