package com.example.gofund.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserData(
    val email:String? = "",
    val password: String? = "",
    val userName : String? = "",
    val birthDate: String? = "",
    val numberOfExpense: Int? = 0,
    val numberOfInvestments: Int? = 0,
    val expenseTypeList: Map<String, ExpenseTypeItem>? = null,
){
    @Exclude
    fun mapTo(){
        // TODO:  
    }
}

data class ExpenseTypeItem(
    val expenseType: String? = "",
    val amount:Int? = 0,
    val title:String? = "",
    val note: String? = "",
    val timeStamp : String? =""
)