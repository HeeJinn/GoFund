    package com.example.gofund.model

    import androidx.compose.runtime.mutableStateOf


    data class UserData(
        val email:String? = "",
        val password: String? = "",
        val userName : String? = "",
        val totalAmount: Int? = 0,
        val initialAmount : Int? = 0,
        val numberOfExpense: Int? = 0,
        val numberOfInvestments: Int? = 0,
        val expenseTypeList: Map<String, ExpenseTypeItem>? = null,
        val reportSummary: Map<String, ReportSummary>? = null
    )

    data class ExpenseTypeItem(
        val key : String? = "",
        val expenseType: String? = "",
        val amount:Int? = 0,
        val title:String? = "",
        val note: String? = "",
        val timeStamp : String? =""
    )

    data class ReportSummary(
        val weeklyExpense: Int? = 0,
        val weeklyInvestment : Int? = 0,
        val monthlyExpense: Int? = 0,
        val monthlyInvestment: Int? = 0,
        val annualExpense: Int? = 0,
        val annualInvestment: Int? = 0,
    )