    package com.example.gofund.model



    data class UserData(
        val email:String? = "",
        val password: String? = "",
        val userName : String? = "",
        val totalAmount: Int? = 0,
        val initialAmount : Int? = 0,
        val numberOfExpense: Int? = 0,
        val numberOfInvestments: Int? = 0,
        val fundLimit: Int? = 0,
        val expenseTypeList: Map<String, ExpenseTypeItem>? = null,
    )

    data class ExpenseTypeItem(
        val key : String? = "",
        val expenseType: String? = "",
        val amount:Int? = 0,
        val title:String? = "",
        val note: String? = "",
        val timeStamp : String? =""
    )
