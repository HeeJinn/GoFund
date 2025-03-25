package com.example.gofund.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.gofund.model.ExpenseTypeItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ExpenseViewModel : ViewModel() {
    private val _expenses = mutableStateListOf<ExpenseTypeItem>()
    val expenses: List<ExpenseTypeItem> get() = _expenses

    private val _isLoading = mutableStateOf(true)
    val isLoading: Boolean get() = _isLoading.value

    private val database = Firebase.database("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val userId = Firebase.auth.currentUser?.uid
    private var listener: ValueEventListener? = null

    init {
        fetchExpenses()
    }

    fun fetchExpenses() {
        // Clear previous listener to avoid duplicates
        listener?.let {
            database.getReference("goFund/$userId/expenseTypeList").removeEventListener(it)
        }

        if (userId == null) {
            _isLoading.value = false
            return
        }

        _isLoading.value = true
        listener = database.getReference("goFund/$userId/expenseTypeList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val tempList = mutableListOf<ExpenseTypeItem>()
                        snapshot.children.forEach { child ->
                            child.getValue(ExpenseTypeItem::class.java)
                                ?.copy(key = child.key ?: "")
                                ?.let { tempList.add(it) }
                        }

                        _expenses.clear()
                        _expenses.addAll(tempList)
                        _isLoading.value = false
                        Log.d("ExpenseVM", "Loaded ${tempList.size} expenses")
                    } catch (e: Exception) {
                        _isLoading.value = false
                        Log.e("ExpenseVM", "Error parsing data", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _isLoading.value = false
                    Log.e("ExpenseVM", "Database error", error.toException())
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        listener?.let {
            database.getReference("goFund/$userId/expenseTypeList").removeEventListener(it)
        }
    }
}