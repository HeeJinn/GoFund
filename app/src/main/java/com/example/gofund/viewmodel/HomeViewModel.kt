package com.example.gofund.viewmodel // Or your preferred package

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gofund.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Sealed interface to represent the state of data loading
sealed interface UserDataResult {
    data object Loading : UserDataResult
    data class Success(val userData: UserData) : UserDataResult
    data class Error(val message: String) : UserDataResult
}

// Make sure UserData class is correctly defined (and remove password if stored!)
// data class UserData(val email: String = "", /* val password: String = "", NO! */ val userName: String = "", val totalAmount: Int = 0, val initialAmount: Int = 0)


// You can add this logic to BSheetViewModel or create a new HomeViewModel
// Using HomeViewModel for clarity here:
class HomeViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Ensure you use the correct Database URL
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var userRef: DatabaseReference? = null
    private var valueEventListener: ValueEventListener? = null

    // StateFlow to hold the result (Loading, Success, or Error)
    private val _userDataState = MutableStateFlow<UserDataResult>(UserDataResult.Loading)
    val userDataState: StateFlow<UserDataResult> = _userDataState.asStateFlow()

    init {
        startObservingUserData()
    }

    private fun startObservingUserData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _userDataState.value = UserDataResult.Error("User not logged in.")
            return // Stop if no user is logged in
        }

        // Define the database reference
        userRef = database.getReference("goFund").child(userId)

        // Create the listener if it doesn't exist
        if (valueEventListener == null) {
            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Try to parse the data
                        val userData = snapshot.getValue(UserData::class.java)
                        if (userData != null) {
                            // Update StateFlow on successful data fetch/update
                            _userDataState.value = UserDataResult.Success(userData)
                            Log.d("HomeViewModel", "Realtime update received: $userData")
                        } else {
                            _userDataState.value = UserDataResult.Error("Failed to parse user data.")
                            Log.w("HomeViewModel", "Snapshot exists but failed to parse UserData")
                        }
                    } else {
                        // Handle case where the user node doesn't exist in DB
                        _userDataState.value = UserDataResult.Error("User data node not found.")
                        Log.w("HomeViewModel", "User data node does not exist for userId: $userId")
                        // You might want to navigate the user to a setup screen here
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors (e.g., permissions denied)
                    Log.e("HomeViewModel", "Firebase listener cancelled: ${error.message}")
                    _userDataState.value = UserDataResult.Error("Database error: ${error.message}")
                    // Remove the listener if cancelled to prevent potential issues
                    stopObservingUserData()
                }
            }
            // Attach the listener
            userRef?.addValueEventListener(valueEventListener!!)
            Log.d("HomeViewModel", "Attached listener for userId: $userId")
        }
    }

    private fun stopObservingUserData() {
        // Remove the listener when it's no longer needed
        valueEventListener?.let { listener ->
            userRef?.removeEventListener(listener)
            Log.d("HomeViewModel", "Removed listener.")
        }
        valueEventListener = null // Clear the reference
    }

    // IMPORTANT: Remove the listener when the ViewModel is cleared (destroyed)
    override fun onCleared() {
        super.onCleared()
        stopObservingUserData()
    }
}