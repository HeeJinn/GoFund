package com.example.gofund.viewmodel // Adjust package name if needed

import android.util.Log // For logging errors
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gofund.model.ExpenseTypeItem // Make sure this path is correct
import com.example.gofund.model.UserData // Make sure this path is correct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.* // Import database components
import com.google.firebase.database.ktx.database // For Firebase.database syntax
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate // Use LocalDate for parsing
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Data class to hold calculated results for a period
data class PeriodTotals(
    val expenseTotal: Int = 0,
    val investmentTotal: Int = 0,
    val expensePercentage: Float = 0f,
    val investmentPercentage: Float = 0f
)

class ReportViewModel : ViewModel() {

    // --- Firebase Instances ---
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Use the specific database URL provided by the user
    private val database: FirebaseDatabase = Firebase.database("https://gofund-1ae38-default-rtdb.asia-southeast1.firebasedatabase.app/")

    // --- StateFlows for calculated results ---
    private val _weeklyTotals = MutableStateFlow(PeriodTotals())
    val weeklyTotals: StateFlow<PeriodTotals> = _weeklyTotals.asStateFlow()

    private val _monthlyTotals = MutableStateFlow(PeriodTotals())
    val monthlyTotals: StateFlow<PeriodTotals> = _monthlyTotals.asStateFlow()

    private val _annualTotals = MutableStateFlow(PeriodTotals())
    val annualTotals: StateFlow<PeriodTotals> = _annualTotals.asStateFlow()

    // --- Error/Loading State ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // --- Timestamp Formatter ---
    private val timestampFormatter = try {
        DateTimeFormatter.ofPattern("M-d-yyyy")
    } catch (e: IllegalArgumentException) {
        Log.e("ReportViewModel", "Invalid timestamp pattern provided: M-d-yyyy", e)
        null
    }

    // Firebase listener reference
    private var userListener: ValueEventListener? = null
    private var userRef: DatabaseReference? = null

    init {
        if (timestampFormatter != null) {
            fetchUserDataAndCalculateTotals()
        } else {
            _isLoading.value = false
            _errorMessage.value = "Date format configuration error."
            Log.e("ReportViewModel", "Cannot fetch data - timestampFormatter is null.")
        }
    }

    private fun fetchUserDataAndCalculateTotals() {
        _isLoading.value = true
        _errorMessage.value = null
        val userId = auth.currentUser?.uid ?: run {
            _errorMessage.value = "User not logged in."
            _isLoading.value = false
            Log.w("ReportViewModel", "Cannot fetch data - user not logged in.")
            return
        }

        // ***** CHANGE HERE: Use "goFund" instead of "users" *****
        userRef = database.reference.child("goFund").child(userId)


        // Create the listener (rest of the function is the same)
        userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                viewModelScope.launch {
                    try {
                        val user = snapshot.getValue(UserData::class.java)
                        if (user != null) {
                            // Check if the expected list exists within the user object
                            if (snapshot.hasChild("expenseTypeList")) {
                                Log.d("ReportViewModel", "User data parsed successfully. User: ${user.userName}, Found expenseTypeList with ~${user.expenseTypeList?.size ?: 0} items.")
                            } else {
                                Log.w("ReportViewModel", "User data parsed, but 'expenseTypeList' field NOT FOUND in snapshot for User: ${user.userName}")
                            }
                            processCalculations(user.expenseTypeList)
                        } else {
                            processCalculations(null)
                        }
                        _errorMessage.value = null
                    } catch (e: DatabaseException) {
                        Log.e("ReportViewModel", "Firebase data mapping error for snapshot: ${snapshot.key}", e)
                        _errorMessage.value = "Error reading user data structure."
                        processCalculations(null)
                    } finally {
                        _isLoading.value = false
                        Log.d("ReportViewModel", "Finished processing data change.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Failed to load data: ${error.message}"
                _isLoading.value = false
                viewModelScope.launch { processCalculations(null) }
            }
        }

        // Add the listener
        userRef?.addValueEventListener(userListener!!)
    }

    // Process calculations for all periods (no changes needed here)
    private fun processCalculations(expenseList: Map<String, ExpenseTypeItem>?) {
        // ... (rest of the function is the same) ...//

        if (timestampFormatter == null) {
            Log.w("ReportViewModel", "Skipping calculations - timestampFormatter is null.")
            _weeklyTotals.value = PeriodTotals()
            _monthlyTotals.value = PeriodTotals()
            _annualTotals.value = PeriodTotals()
            return
        }
        Log.d("ReportViewModel", "Processing calculations for ${expenseList?.size ?: 0} items.")
        _weeklyTotals.value = calculateTotalsForPeriod(expenseList, TimePeriod.WEEKLY, timestampFormatter)
        _monthlyTotals.value = calculateTotalsForPeriod(expenseList, TimePeriod.MONTHLY, timestampFormatter)
        _annualTotals.value = calculateTotalsForPeriod(expenseList, TimePeriod.ANNUALLY, timestampFormatter)
        // Log final computed values
        Log.i("ReportViewModel", "Calculations Complete -> Weekly: E=${_weeklyTotals.value.expenseTotal}, I=${_weeklyTotals.value.investmentTotal} | Monthly: E=${_monthlyTotals.value.expenseTotal}, I=${_monthlyTotals.value.investmentTotal} | Annual: E=${_annualTotals.value.expenseTotal}, I=${_annualTotals.value.investmentTotal}")
    }

    // Enum to define time periods (no changes needed here)
    private enum class TimePeriod { WEEKLY, MONTHLY, ANNUALLY }

    // Generic calculation function (no changes needed here)
    private fun calculateTotalsForPeriod(
        expenseList: Map<String, ExpenseTypeItem>?,
        period: TimePeriod,
        formatter: DateTimeFormatter
    ): PeriodTotals {
        var expenseTotal = 0
        var investmentTotal = 0

        if (expenseList == null) {
            Log.d("ReportViewModel", "calculateTotalsForPeriod: expenseList is null for period $period.")
            return PeriodTotals()
        }

        val now = LocalDateTime.now()
        var itemsInPeriod = 0
        var investmentItemsFoundInPeriod = 0 // Specific counter for investments

        expenseList.values.forEach { item ->
            val itemDateTime = parseTimestamp(item.timeStamp, formatter)

            if (itemDateTime != null) {
                if (isWithinPeriod(itemDateTime, now, period)) {
                    itemsInPeriod++
                    val amount = item.amount ?: 0
                    val itemTypeLower = item.expenseType?.lowercase() // Calculate once

                    when (itemTypeLower) {
                        "expense" -> expenseTotal += amount
                        "investment" -> {
                            investmentTotal += amount
                            investmentItemsFoundInPeriod++ // Increment investment counter
                        }
                        else -> Log.w("ReportViewModel", "Unknown expense type '${item.expenseType}' for item with title '${item.title}' (Amount: $amount)")
                    }
                }
            }
            // parseTimestamp logs parsing errors
        }
        // More detailed log
        Log.d("ReportViewModel", "calculateTotalsForPeriod ($period): Processed ${expenseList.size} items total. Found $itemsInPeriod items within period ($investmentItemsFoundInPeriod were 'investment'). Calculated Expense: $expenseTotal, Investment: $investmentTotal")


        // Calculate percentages (no changes needed)
        val totalOutlay = (expenseTotal + investmentTotal).toFloat()
        val expensePercent = if (totalOutlay == 0f) 0f else (expenseTotal / totalOutlay) * 100f
        val investmentPercent = if (totalOutlay == 0f) 0f else (investmentTotal / totalOutlay) * 100f

        return PeriodTotals(expenseTotal, investmentTotal, expensePercent, investmentPercent)
    }

    // parseTimestamp function (no changes needed here)
    private fun parseTimestamp(timestampString: String?, formatter: DateTimeFormatter): LocalDateTime? {
        // ... (same as before) ...//
        if (timestampString.isNullOrBlank()) {
            // Log.w("ReportViewModel", "Attempted to parse null or blank timestamp.") // Optional: reduce noise if blank timestamps are expected
            return null
        }
        return try {
            val localDate = LocalDate.parse(timestampString, formatter)
            localDate.atStartOfDay()
        } catch (e: DateTimeParseException) {
            Log.w("ReportViewModel", "Timestamp parse error for string '$timestampString' using format '${formatter}'. Error: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("ReportViewModel", "Unexpected error parsing timestamp '$timestampString'", e)
            null
        }
    }

    // Inside ReportViewModel class

    /**
     * Checks if the item's LocalDateTime falls within the specified TimePeriod relative to 'now'.
     * Weekly = Last 7 Days
     * Monthly = Last 30 Days (Changed)
     * Annually = Last 365 Days (Changed)
     */
    private fun isWithinPeriod(itemDateTime: LocalDateTime, now: LocalDateTime, period: TimePeriod): Boolean {
        return when (period) {
            TimePeriod.WEEKLY -> {
                // Keep as: Last 7 days from 'now'
                val sevenDaysAgo = now.minusDays(7)
                // Check if itemDateTime is ON or AFTER sevenDaysAgo AND BEFORE now (plus a second for inclusivity)
                !itemDateTime.isBefore(sevenDaysAgo) && itemDateTime.isBefore(now.plusSeconds(1))
            }
            TimePeriod.MONTHLY -> {
                // *** CHANGE: Calculate for Last 30 days from 'now' ***
                val thirtyDaysAgo = now.minusDays(30)
                // Check if itemDateTime is ON or AFTER thirtyDaysAgo AND BEFORE now
                !itemDateTime.isBefore(thirtyDaysAgo) && itemDateTime.isBefore(now.plusSeconds(1))
            }
            TimePeriod.ANNUALLY -> {
                // *** CHANGE: Calculate for Last 365 days from 'now' ***
                val yearAgo = now.minusDays(365)
                // Check if itemDateTime is ON or AFTER yearAgo AND BEFORE now
                !itemDateTime.isBefore(yearAgo) && itemDateTime.isBefore(now.plusSeconds(1))
            }
        }
    }

    // ... (Rest of the ViewModel remains the same) ...

    // onCleared function (no changes needed here)
    override fun onCleared() {
        // ... (same as before) ...//
        super.onCleared()
        userListener?.let { listener ->
            userRef?.removeEventListener(listener)
            Log.d("ReportViewModel", "Firebase listener removed.")
        }
        userListener = null
        userRef = null
    }
}