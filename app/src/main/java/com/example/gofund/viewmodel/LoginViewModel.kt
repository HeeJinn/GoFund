import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gofund.model.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

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
                                    createUserData(email, password)

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
    private fun createUserData(email: String, password: String){
        val firebase = Firebase.database("hhttps://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val dbRef = firebase.getReference("goFund")
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val userData = UserData(email = email, password= password)
        dbRef.child(userID.toString()).setValue(userData)
            .addOnCompleteListener {
                Log.d("USER_DATA", "user created")
        }
            .addOnFailureListener {
                Log.d("USER_DATA", it.message.toString())
            }
    }
}
