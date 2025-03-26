package com.example.gofund.viewmodel

import androidx.lifecycle.ViewModel

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.example.gofund.model.ExpenseTypeItem
import com.example.gofund.view.checkIfHigherThanMax
import com.example.gofund.view.checkIfLowerThanZero
import java.lang.Exception

class CreateExpenseViewModel : ViewModel() {
    private val firebase = Firebase.database("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val userID = Firebase.auth.currentUser?.uid
    private val userRef = firebase.getReference("goFund").child(userID!!)

    fun addNewExpense(
        expenseType: String,  // This should be either "Expense" or "Investment"
        amount: Int,
        title: String,
        note: String,
        timeStamp: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userRef.child("expenseTypeList").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val expenseTypeList = task.result.value as? Map<*, *>
                    val expenseItem = ExpenseTypeItem(
                        key = "",
                        expenseType = expenseType,
                        amount = amount,
                        title = title,
                        note = note,
                        timeStamp = timeStamp
                    )

                    if (expenseTypeList == null) {
                        userRef.child("expenseTypeList").setValue(hashMapOf<String, Any>())
                            .addOnSuccessListener {
                                addExpenseWithGeneratedKey(expenseItem, onSuccess, onFailure)
                            }
                            .addOnFailureListener(onFailure)
                    } else {
                        addExpenseWithGeneratedKey(expenseItem, onSuccess, onFailure)
                    }
                } else {
                    onFailure(task.exception ?: Exception("Failed to check expenseTypeList"))
                }
            }
    }

    private fun addExpenseWithGeneratedKey(
        expenseItem: ExpenseTypeItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val newExpenseRef = userRef.child("expenseTypeList").push()
        val key = newExpenseRef.key ?: ""

        val expenseWithKey = expenseItem.copy(key = key)
        val amountValue = expenseItem.amount ?: 0
        val isInvestment = expenseItem.expenseType.equals("Investment", ignoreCase = true)

        // Create transaction updates
        val updates = hashMapOf<String, Any>(
            "expenseTypeList/$key" to expenseWithKey
        )

        // Get current values first to calculate new totals
        userRef.child("totalAmount").get().addOnSuccessListener { totalAmountSnapshot ->
            val currentTotalAmount = totalAmountSnapshot.getValue(Int::class.java) ?: 0

            if (currentTotalAmount < amountValue) {
                onFailure(Exception("Total amount cannot be lower than expense amount"))
                return@addOnSuccessListener
            }

            // Update the appropriate counter based on expense type
            val counterField = if (isInvestment) "numberOfInvestments" else "numberOfExpense"

            userRef.child(counterField).get().addOnSuccessListener { counterSnapshot ->
                val currentCount = counterSnapshot.getValue(Int::class.java) ?: 0

                updates["totalAmount"] = checkIfLowerThanZero(currentTotalAmount - amountValue)
                updates[counterField] = checkIfHigherThanMax(currentCount + 1)

                userRef.updateChildren(updates)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener(onFailure)
            }.addOnFailureListener(onFailure)
        }.addOnFailureListener(onFailure)
    }
}