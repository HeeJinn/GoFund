package com.example.gofund.viewmodel // Or your relevant package

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gofund.model.UserData // Assuming UserData has email/userName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Result type for profile loading
sealed interface ProfileLoadResult {
    data object Loading : ProfileLoadResult
    data class Success(val email: String, val userName: String) : ProfileLoadResult
    data class Error(val message: String) : ProfileLoadResult
}

// Result type for update operations
sealed class UpdateResult {
    data object Success : UpdateResult()
    data class Error(val message: String) : UpdateResult()
    data object RequiresReAuthentication : UpdateResult()
}


class AccountViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Ensure correct DB URL
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var profileListener: ValueEventListener? = null
    private var userDbRef: DatabaseReference? = null

    // --- State for Loading Profile Data ---
    private val _profileState = MutableStateFlow<ProfileLoadResult>(ProfileLoadResult.Loading)
    val profileState: StateFlow<ProfileLoadResult> = _profileState.asStateFlow()

    // --- State for Update Operations ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- Event Flows for Feedback ---
    private val _updateEmailEvent = MutableSharedFlow<UpdateResult>()
    val updateEmailEvent: SharedFlow<UpdateResult> = _updateEmailEvent.asSharedFlow()

    private val _updateUsernameEvent = MutableSharedFlow<UpdateResult>() // Separate event for username
    val updateUsernameEvent: SharedFlow<UpdateResult> = _updateUsernameEvent.asSharedFlow()


    init {
        loadUserProfile()
    }

    // --- Load Initial Profile Data ---
    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _profileState.value = ProfileLoadResult.Error("User not logged in.")
            return
        }
        _profileState.value = ProfileLoadResult.Loading

        userDbRef = database.getReference("goFund").child(userId)

        // Use single value event listener if only needed initially,
        // or addValueEventListener if profile can change elsewhere
        profileListener = userDbRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(UserData::class.java)
                    val currentAuthEmail = auth.currentUser?.email // Get email from Auth primarily
                    if (userData != null) {
                        _profileState.value = ProfileLoadResult.Success(
                            email = currentAuthEmail ?: userData.email ?: "", // Prefer Auth email
                            userName = userData.userName ?: ""
                        )
                        Log.d("AccountViewModel", "Profile loaded: Email=${currentAuthEmail ?: userData.email}, Name=${userData.userName}")
                    } else {
                        _profileState.value = ProfileLoadResult.Error("Failed to parse profile data.")
                    }
                } else {
                    _profileState.value = ProfileLoadResult.Error("Profile data not found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AccountViewModel", "Failed to load profile", error.toException())
                _profileState.value = ProfileLoadResult.Error("Error loading profile: ${error.message}")
            }
        })
    }

    // --- Update Email Logic ---
    fun updateUserEmail(newEmail: String) {
        val user = auth.currentUser
        val trimmedEmail = newEmail.trim()

        // Perform validations
        if (user == null) { emitUpdateResult(_updateEmailEvent, UpdateResult.Error("User not logged in.")); return }
        if (trimmedEmail.isBlank()) { emitUpdateResult(_updateEmailEvent, UpdateResult.Error("Email cannot be empty.")); return }
        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) { emitUpdateResult(_updateEmailEvent, UpdateResult.Error("Invalid email format.")); return }
        if (trimmedEmail == user.email) { emitUpdateResult(_updateEmailEvent, UpdateResult.Error("New email is same as current.")); return }

        _isLoading.value = true
        Log.d("AccountViewModel", "Attempting email update...")

        user.updateEmail(trimmedEmail).addOnCompleteListener { task ->
            _isLoading.value = false
            if (task.isSuccessful) {
                Log.i("AccountViewModel", "Firebase Auth email updated successfully.")
                // Also update in Realtime DB
                updateUserProfileData(mapOf("email" to trimmedEmail)) { success, error ->
                    if (success) {
                        emitUpdateResult(_updateEmailEvent, UpdateResult.Success)
                        // Optional: Trigger verification email for new address
                        user.sendEmailVerification().addOnCompleteListener { Log.d("AccountVM", "Verification Sent: ${it.isSuccessful}") }
                    } else {
                        emitUpdateResult(_updateEmailEvent, UpdateResult.Error(error ?: "Failed to update email in database."))
                    }
                }
            } else {
                handleAuthExceptions(task.exception, _updateEmailEvent)
            }
        }
    }

    // --- Update Username Logic ---
    fun updateUserName(newUserName: String) {
        val userId = auth.currentUser?.uid
        val trimmedUserName = newUserName.trim()

        if (userId == null) { emitUpdateResult(_updateUsernameEvent, UpdateResult.Error("User not logged in.")); return }
        if (trimmedUserName.isBlank()) { emitUpdateResult(_updateUsernameEvent, UpdateResult.Error("Username cannot be empty.")); return }
        if (trimmedUserName.length > 13) { emitUpdateResult(_updateUsernameEvent, UpdateResult.Error("Username exceeds 13 characters.")); return }

        // Compare with current username before updating to avoid unnecessary writes
        val currentState = _profileState.value
        if (currentState is ProfileLoadResult.Success && trimmedUserName == currentState.userName) {
            emitUpdateResult(_updateUsernameEvent, UpdateResult.Error("Username is the same."))
            return
        }

        _isLoading.value = true
        Log.d("AccountViewModel", "Attempting username update...")

        // Update only the username field in Realtime DB
        updateUserProfileData(mapOf("userName" to trimmedUserName)) { success, error ->
            _isLoading.value = false
            if (success) {
                Log.i("AccountViewModel", "Username updated successfully.")
                emitUpdateResult(_updateUsernameEvent, UpdateResult.Success)
            } else {
                Log.e("AccountViewModel", "Failed to update username.")
                emitUpdateResult(_updateUsernameEvent, UpdateResult.Error(error ?: "Failed to update username."))
            }
        }
    }

    // --- Helper to update specific fields in Realtime DB ---
    private fun updateUserProfileData(updates: Map<String, Any?>, onComplete: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) { onComplete(false, "User not logged in"); return }
        database.getReference("goFund").child(userId)
            .updateChildren(updates)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    // --- Helper to handle common Auth exceptions ---
    private fun handleAuthExceptions(exception: Exception?, eventFlow: MutableSharedFlow<UpdateResult>) {
        val result = when (exception) {
            is FirebaseAuthRecentLoginRequiredException -> UpdateResult.RequiresReAuthentication
            is FirebaseAuthUserCollisionException -> UpdateResult.Error("Email already in use.")
            is FirebaseAuthInvalidCredentialsException -> UpdateResult.Error("Invalid email format.")
            else -> UpdateResult.Error("Operation failed: ${exception?.message ?: "Unknown error"}")
        }
        viewModelScope.launch { eventFlow.emit(result) }
    }

    // --- Helper to emit results on SharedFlow ---
    private fun emitUpdateResult(flow: MutableSharedFlow<UpdateResult>, result: UpdateResult) {
        viewModelScope.launch { flow.emit(result) }
    }


    // --- Cleanup Listener ---
    override fun onCleared() {
        super.onCleared()
        profileListener?.let { listener -> userDbRef?.removeEventListener(listener) }
        profileListener = null
        userDbRef = null
        Log.d("AccountViewModel", "ViewModel cleared, listener removed.")
    }
}