package com.example.gofund.navigations

const val ROOT_ROUTE = "root"
const val CONTENT_ROUTE = "content"

const val ARGUMENT_EXPENSE_TYPE = "expense_type"
const val ARGUMENT_AMOUNT_TYPE = "amount"
const val ARGUMENT_TITLE_TYPE = "title"
const val ARGUMENT_NOTE = "note"
const val ARGUMENT_TIMESTAMP = "timestamp"

sealed class Screen(val route: String) {
    object LoginScreen: Screen(route = "login_screen")
    object RegisterScreen: Screen(route = "register_screen")
    object ContentScreen: Screen(route = "content_screen")
    object DetailScreen : Screen(route = "detail_screen/{$ARGUMENT_EXPENSE_TYPE}/{$ARGUMENT_AMOUNT_TYPE}/{$ARGUMENT_TITLE_TYPE}/{$ARGUMENT_NOTE}/{$ARGUMENT_TIMESTAMP}"){
        fun passNote(expenseType: String,amount: Int,title: String,note: String, timestamp: String) = "detail_screen/$expenseType/$amount/$title/$note/$timestamp"

    }

}