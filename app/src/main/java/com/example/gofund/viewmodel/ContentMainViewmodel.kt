package com.example.gofund.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gofund.model.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContentMainViewmodel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebase = Firebase.database("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var valueEventListener: ValueEventListener? = null
    private var userRef: DatabaseReference? = null

    private val _fundLimit = MutableStateFlow(0)
    val fundLimit : StateFlow<Int> = _fundLimit.asStateFlow()

    init {
        startObservation()
    }

    private fun startObservation() {
        val userID = auth.currentUser?.uid
        if (userID == null) {
            Log.d("CONTENT_VIEWMODEL", "User not logged in")
            return
        }
        userRef = firebase.getReference("goFund").child(userID).child("fundLimit")
        if (valueEventListener == null) {
            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(Int::class.java)
                        if (data != null) {
                            _fundLimit.value = data

                        }
                    }else
                        _fundLimit.value = 0
                }

                override fun onCancelled(error: DatabaseError) {
                    valueEventListener.let {
                        userRef?.removeEventListener(it!!)
                    }
                }
            }
        }
        userRef?.addValueEventListener(valueEventListener!!)
        Log.d("CONTENT_VIEWMODEL", "Amount: $_fundLimit")
    }
    fun resetFund(){
        val userID = auth.currentUser?.uid
        if (userID == null) {
            Log.d("CONTENT_VIEWMODEL", "User not logged in")
            return
        }
        val userRef = firebase.getReference("goFund").child(userID)
        val updates = mapOf<String, Any?>("fundLimit" to null)
        userRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("CONTENT_VIEWMODEL", "Fund reset successfully")
            }
            .addOnFailureListener { e ->
                Log.e("CONTENT_VIEWMODEL", "Failed to reset fund", e)
            }

    }
}