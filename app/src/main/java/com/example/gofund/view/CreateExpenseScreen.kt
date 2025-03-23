package com.example.gofund.view

import android.icu.text.DateFormat
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gofund.R
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.PoppinsFamily
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpenseScreen(
    navController: NavController,
) {
    var selectedIndex by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") } // Store amount as a string initially
    var note by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("M-d-yyyy", Locale.getDefault()) // Use "M-d-yyyy" for M-day-year
    val timeStamp = dateFormat.format(calendar) // Format the date
    Log.d("TIME_STAMP", timeStamp) // Output: 10-10-2024 (or the current date)

    var maxTitleCharacters = 13
    var maxAmountCharacters = 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SegmentedButtonAndImageForExpense(
            selectedIndex = selectedIndex,
            onIndexChange = { newIndex ->
                selectedIndex = newIndex
            }
        )

        ExpenseCard(
            title = title,
            amount = amount,
            note = note,
            onAmountValueChange = { newAmount ->
                // Validate input: only allow digits and enforce max length
                if (newAmount.all { it.isDigit() } && newAmount.length <= maxAmountCharacters) {
                    amount = newAmount
                }
            },
            onValueTitleChange = { newTitle ->
                if (newTitle.length <= maxTitleCharacters) {
                    title = newTitle
                }
            },
            onNoteValueChange = {newNote ->
                if (newNote.length <= 100){
                    note = newNote
                }

            },
            onClearButtonClick = {
                selectedIndex = 0
                title = ""
                amount = ""
                note = ""
                Log.d("CONTENT_BUTTON", "Clear Button Clicked")
            },
            onAddButtonClick = {
                Log.d("CONTENT_BUTTON", "Add Button Clicked")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtonAndImageForExpense(
    selectedIndex: Int, 
    onIndexChange: (Int) -> Unit
) {
    val options = listOf("Expense", "Investment")
    val alphaImageExpense = if (selectedIndex == 0) 1f else 0.5f
    val alphaImageInvestment = if (selectedIndex == 1) 1f else 0.5f

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                onClick = {
                    onIndexChange(index) // Update the selectedIndex
                    Log.d("SELECTED_INDEX", selectedIndex.toString())
                },
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                selected = selectedIndex == index,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = Color.White,
                    inactiveContainerColor = Color.Transparent,
                    inactiveContentColor = Color.Black,

                ),
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(
                    text = option,
                    fontFamily = PoppinsFamily,
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(130.dp),
            alpha = alphaImageExpense,
            painter = painterResource(id = R.drawable.expense),
            contentDescription = "expense_image"
        )
        Image(
            modifier = Modifier
                .size(130.dp),
            alpha = alphaImageInvestment,
            painter = painterResource(id = R.drawable.investment),
            contentDescription = "expense_image"
        )
    }
}

@Composable
fun ExpenseCard(title: String, amount: String, note: String ,onValueTitleChange: (String) -> Unit, onAmountValueChange: (String) -> Unit, onNoteValueChange: (String) -> Unit, onClearButtonClick: () -> Unit, onAddButtonClick: () -> Unit){
    var leadingTitleIcon = painterResource(R.drawable.title_vector)
    var leadingAmountIcon = painterResource(R.drawable.ammount_vector)
    var leadingNoteIcon = painterResource(R.drawable.note_vector)
    var titlePlaceHolder = "Title"
    var amountPlaceHolder = "Amount"
    var notePlaceholder = "Note"
    var supportingTextTitle = "*13-max characters"
    var supportingAmount = "*6-max characters"
    var supportingNoteText = "*100-max characters"
    var keyboardTypeAmount = KeyboardType.Number
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: populate this ui later hehe
            TextFieldForCreateExpense(value = title, onValueChange = onValueTitleChange, leadingIcon = leadingTitleIcon, placeHolder = titlePlaceHolder, supportingText = supportingTextTitle)
            TextFieldForCreateExpense(value = amount, onValueChange = onAmountValueChange, leadingIcon = leadingAmountIcon, placeHolder = amountPlaceHolder, supportingText = supportingAmount, keyboardType = keyboardTypeAmount)
            TextFieldForCreateExpense(value = note, onValueChange = onNoteValueChange, leadingIcon = leadingNoteIcon, placeHolder = notePlaceholder, supportingText = supportingNoteText, maxLines = 10, minLines = 1, singleLine = false)
        }
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            modifier = Modifier
                .weight(1f)
                .padding(end = 5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(width = 0.dp, color = Color.Transparent),
            onClick = onClearButtonClick
        ) {
            Text(
                modifier = Modifier,
                text = "Clear",
                textAlign = TextAlign.Center
            )
        }
        OutlinedButton(
            modifier = Modifier
                .weight(1f)
                .padding(start = 5.dp),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
            onClick = onAddButtonClick
        ) {
            Text(
                modifier = Modifier,
                text = "Add",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TextFieldForCreateExpense(value: String, onValueChange: (String) -> Unit, leadingIcon : Painter, maxLines : Int = 1, minLines: Int = 1, placeHolder: String, supportingText: String, keyboardType: KeyboardType = KeyboardType.Text, singleLine: Boolean = true){
    var isFocus = LocalFocusManager.current

    TextField(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .width(300.dp),
        value = value,

        textStyle = TextStyle(
            fontFamily = IntroFamily,
            color = MaterialTheme.colorScheme.tertiary
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            ),
        maxLines = maxLines,
        minLines = minLines,
        supportingText = {
            Text(
                text = supportingText,
                color = Color.White
            )
        },
        singleLine = singleLine,
        leadingIcon = {
            Icon(painter = leadingIcon, contentDescription = "icon_title")
        },
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.medium,
        placeholder = {
            Text(
                text = placeHolder,
                color = Color.Gray,
                fontFamily = IntroFamily
            )
        },
        keyboardActions = KeyboardActions(onDone = {isFocus.clearFocus()}),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = keyboardType)
    )

}

@Preview(showBackground = true)
@Composable
fun ShowCreateExpenseScreen(){
    val navController = rememberNavController()
    CreateExpenseScreen(navController = navController)

}
