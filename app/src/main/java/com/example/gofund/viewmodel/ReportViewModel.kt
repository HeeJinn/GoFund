package com.example.gofund.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.gofund.model.ExpenseTypeItem
import com.example.gofund.model.ReportSummary
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue

class ReportViewModel : ViewModel() {
    // UI State
    val selectedIndex = mutableStateOf(0)
    val reportOptions = listOf("Weekly", "Monthly", "Annually")
    val isLoading = mutableStateOf(true)
    val errorState = mutableStateOf<String?>(null)

    // Report Data
    private val _reportSummary = mutableStateOf(ReportSummary())
    val reportSummary: ReportSummary get() = _reportSummary.value

    private val database = Firebase.database.reference
    private val userId = Firebase.auth.currentUser?.uid

    init {
        loadReportData()
    }

    fun loadReportData() {
        if (userId == null) {
            errorState.value = "User not authenticated"
            isLoading.value = false
            return
        }

        isLoading.value = true
        errorState.value = null

        database.child("goFund/$userId/reportSummary")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            // Use existing report summary
                            val summary = snapshot.getValue(ReportSummary::class.java)
                            _reportSummary.value = summary ?: ReportSummary()
                            Log.d("ReportVM", "Loaded existing report summary")
                        } else {
                            // Calculate and save new report
                            calculateAndSaveReport()
                            return  // Exit early, calculateAndSaveReport will handle loading state
                        }
                    } catch (e: Exception) {
                        errorState.value = "Failed to parse report data"
                        Log.e("ReportVM", "Error parsing report data", e)
                    }
                    isLoading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    errorState.value = "Failed to load report data"
                    isLoading.value = false
                    Log.e("ReportVM", "Failed to check report summary", error.toException())
                }
            })
    }

    private fun calculateAndSaveReport() {
        database.child("goFund/$userId/expenseTypeList")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (!snapshot.exists()) {
                            _reportSummary.value = ReportSummary()  // Empty report
                            saveReportSummary(ReportSummary())  // Save empty report to prevent repeated calculations
                            return
                        }

                        val now = Calendar.getInstance()
                        val weeklyExpense = mutableListOf<Int>()
                        val weeklyInvestment = mutableListOf<Int>()
                        val monthlyExpense = mutableListOf<Int>()
                        val monthlyInvestment = mutableListOf<Int>()
                        val annualExpense = mutableListOf<Int>()
                        val annualInvestment = mutableListOf<Int>()

                        snapshot.children.forEach { child ->
                            val expense = child.getValue(ExpenseTypeItem::class.java)
                            expense?.let {
                                val amount = it.amount ?: 0
                                val date = parseDate(it.timeStamp)
                                if (date != null) {
                                    when {
                                        isSameWeek(date, now) -> {
                                            if (amount > 0) weeklyExpense.add(amount)
                                            else weeklyInvestment.add(amount.absoluteValue)
                                        }
                                        isSameMonth(date, now) -> {
                                            if (amount > 0) monthlyExpense.add(amount)
                                            else monthlyInvestment.add(amount.absoluteValue)
                                        }
                                        isSameYear(date, now) -> {
                                            if (amount > 0) annualExpense.add(amount)
                                            else annualInvestment.add(amount.absoluteValue)
                                        }
                                    }
                                }
                            }
                        }

                        val newSummary = ReportSummary(
                            weeklyExpense = weeklyExpense.sum(),
                            weeklyInvestment = weeklyInvestment.sum(),
                            monthlyExpense = monthlyExpense.sum(),
                            monthlyInvestment = monthlyInvestment.sum(),
                            annualExpense = annualExpense.sum(),
                            annualInvestment = annualInvestment.sum()
                        )

                        saveReportSummary(newSummary)

                    } catch (e: Exception) {
                        errorState.value = "Error calculating report"
                        isLoading.value = false
                        Log.e("ReportVM", "Error calculating report", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    errorState.value = "Failed to load expenses"
                    isLoading.value = false
                    Log.e("ReportVM", "Failed to load expenses", error.toException())
                }
            })
    }

    private fun saveReportSummary(summary: ReportSummary) {
        database.child("goFund/$userId/reportSummary")
            .setValue(summary)
            .addOnSuccessListener {
                _reportSummary.value = summary
                Log.d("ReportVM", "Saved new report summary")
            }
            .addOnFailureListener {
                errorState.value = "Failed to save report"
                Log.e("ReportVM", "Failed to save report", it)
            }
            .addOnCompleteListener {
                isLoading.value = false
            }
    }

    private fun parseDate(dateString: String?): Calendar? {
        if (dateString.isNullOrBlank()) return null
        return try {
            val formats = listOf(
                SimpleDateFormat("M-d-yyyy", Locale.getDefault()),
                SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            )

            var parsedDate: Calendar? = null
            for (format in formats) {
                try {
                    val date = format.parse(dateString) ?: continue
                    parsedDate = Calendar.getInstance().apply { time = date }
                    break
                } catch (e: Exception) {
                    continue
                }
            }
            parsedDate
        } catch (e: Exception) {
            null
        }
    }

    private fun isSameWeek(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.WEEK_OF_YEAR) == date2.get(Calendar.WEEK_OF_YEAR)
    }

    private fun isSameMonth(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
    }

    private fun isSameYear(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
    }

    fun getCurrentExpenseTotal(): Int {
        return when (selectedIndex.value) {
            0 -> reportSummary.weeklyExpense ?: 0
            1 -> reportSummary.monthlyExpense ?: 0
            else -> reportSummary.annualExpense ?: 0
        }
    }

    fun getCurrentInvestmentTotal(): Int {
        return when (selectedIndex.value) {
            0 -> reportSummary.weeklyInvestment ?: 0
            1 -> reportSummary.monthlyInvestment ?: 0
            else -> reportSummary.annualInvestment ?: 0
        }
    }

    fun retryLoading() {
        loadReportData()
    }
}