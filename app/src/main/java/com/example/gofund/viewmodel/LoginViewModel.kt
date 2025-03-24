import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gofund.model.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth // Use Firebase.auth

    fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        onSuccess()
                    } else {
                        auth.signOut() // Prevent unverified users from staying signed in
                        onFailure("Please verify your email before logging in.")
                    }
                } else {
                    onFailure("Login failed: ${task.exception?.message ?: "Unknown error"}") // More specific message
                }
            }
    }


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
            val userData = UserData(email = email, password = password, userName = userName, totalAmount = totalAmount, initialAmount = initialAmount)
            dbRef.child(userID).setValue(userData)
                .addOnSuccessListener {
                    Log.d("USER_DATA", "user created")
                    onComplete(true, null) //

                }
                .addOnFailureListener { e ->
                    Log.e("USER_DATA", "Error creating user data: ${e.message}")
                    onComplete(false, e.message) //
                }
        } else {
            Log.e("USER_DATA", "User ID is null. Cannot create user data.")
            onComplete(false, "User ID is null")
        }
    }
}