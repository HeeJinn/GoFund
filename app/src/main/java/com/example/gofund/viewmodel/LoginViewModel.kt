import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                    onFailure("Login failed.")
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "An error occurred.")
            }
    }


    fun signUp(email: String, username: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { user ->
                        user.sendEmailVerification()
                            .addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    Log.d("sendEmail", "Email verification sent")
                                    onSuccess()  // Ensure success callback is called
                                } else {
                                    onFailure("Failed to send verification email.")
                                }
                            }
                    } ?: onFailure("User creation failed.")
                } else {
                    onFailure("Sign-up failed")
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "An error occurred.")
            }
    }
}
