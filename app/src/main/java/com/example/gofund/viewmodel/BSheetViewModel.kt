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


}