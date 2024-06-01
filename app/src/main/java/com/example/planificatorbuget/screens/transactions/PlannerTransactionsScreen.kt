package com.example.planificatorbuget.screens.transactions

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.FABContent
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.utils.gradientBackgroundBrush
import java.util.Calendar

@Preview
@Composable
fun PlannerTransactionsScreen(navController: NavController = NavController(LocalContext.current)) {

    val originalListOfTransactions = remember {
        mutableStateOf(
            listOf(
                TransactionModel(
                    "1",
                    "1",
                    20.0,
                    "Venit",
                    "Salariu",
                    "08/17/2024",
                    "Mancare petru caine",
                    "multe chestii pentru caine i-am cumparat boabe si altele"
                ),
                TransactionModel(
                    "2",
                    "1",
                    20.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "06/01/2024",
                    "Service masina"
                ),
                TransactionModel("3", "1", 20.0, "Venit", "Salariu", "08/17/2023", "1000"),
                TransactionModel(
                    "4",
                    "1",
                    30.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "05/17/2024",
                    "Factura telefon"
                ),
                TransactionModel("5", "1", 20.0, "Venit", "Salariu", "08/17/2023", "1000"),
                TransactionModel(
                    "6",
                    "1",
                    20.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "05/17/2024",
                    "Altceva"
                ),
                TransactionModel("7", "1", 20.0, "Venit", "Salariu", "08/17/2023", "1000"),
                TransactionModel(
                    "8",
                    "1",
                    60.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "08/17/2023",
                    "Alta factura"
                ),
                TransactionModel("9", "1", 10.0, "Venit", "Salariu", "08/17/2023", "1000"),
                TransactionModel(
                    "10",
                    "1",
                    6.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "08/17/2023",
                    "Ceva"
                ),
            )
        )
    }
    val filteredListOfTransactions = remember { mutableStateOf(originalListOfTransactions.value) }


    Box(
        modifier = Modifier.background(
            brush = gradientBackgroundBrush(
                isVerticalGradient = true,
                colors = listOf(
                    Color(0xFF7F9191),
                    Color(0xffc3c3d8),
                    Color(0xff00d4ff)
                )
            )
        )
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Tranzactii",
                    haveNotifications = false,
                    isHomeScreen = false,
                    navController = navController
                ) {
                    navController.popBackStack()
                }
            },
            bottomBar = {
                NavigationBarComponent(navController = navController)
            },
            floatingActionButton = {
                FABContent {

                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = Color.Transparent
        ) { paddingValues ->
            Card(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(bottom = 10.dp, start = 7.dp, end = 7.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    SearchTransactionsByDateForm { date ->
                        if (date.isEmpty())
                            filteredListOfTransactions.value = originalListOfTransactions.value
                        else
                            filteredListOfTransactions.value =
                                originalListOfTransactions.value.filter {
                                    it.transactionDate == date
                                }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    FilterAndSortTransactions(
                        onSort = {
                            filteredListOfTransactions.value =
                                filteredListOfTransactions.value.sortedBy { it.amount }
                        },
                        onFilter = { type, categories ->
                            filteredListOfTransactions.value =
                                originalListOfTransactions.value.filter {
                                    (type.isEmpty() || it.transactionType == type) &&
                                            (categories.isEmpty() || categories.contains(it.categoryId))
                                }
                        }) { query ->
                        if (query.isEmpty()) {
                            filteredListOfTransactions.value = originalListOfTransactions.value
                        } else {
                            filteredListOfTransactions.value =
                                originalListOfTransactions.value.filter {
                                    it.transactionTitle.contains(query, ignoreCase = true)
                                }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TransactionsList(lisOfTransactions = filteredListOfTransactions.value)
                }
            }

        }

    }

}


@Composable
private fun FilterAndSortTransactions(
    onSort: () -> Unit = { },
    onFilter: (String, List<String>) -> Unit = { _, _ -> },
    onSearch: (String) -> Unit = { },
) {

    val searchQueryState = rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val showDialog = rememberSaveable { mutableStateOf(false) }
    val selectedType = rememberSaveable { mutableStateOf("") }
    val selectedCategories = rememberSaveable { mutableStateOf(listOf<String>()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { onSort() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(5.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                Text(text = "Sorteaza")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                showDialog.value = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(5.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
                Text(text = "Filtreaza")
            }
        }
        Spacer(modifier = Modifier.width(30.dp))
        OutlinedTextField(
            value = searchQueryState.value,
            onValueChange = { searchQueryState.value = it },
            label = { Text("Cauta tranzactii", style = MaterialTheme.typography.labelLarge) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(),
            shape = RoundedCornerShape(30),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions {
                onSearch(searchQueryState.value.trim())
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        )

    }
    FilterDialog(
        showDialog = showDialog,
        onFilterApply = onFilter,
        selectedType = selectedType,
        selectedCategories = selectedCategories
    )
}

@Composable
private fun SearchTransactionsByDateForm(
    onSelectedDate: (String) -> Unit = { }
) {
    var selectedDate by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
            Text(
                text = "Tranzactii",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(50.dp))
        DatePickerField(
            label = "Alege o data",
            selectedDate = selectedDate,
            onDateSelected = {
                selectedDate = it
                onSelectedDate(selectedDate)
            },
            onClearDate = {
                selectedDate = ""
                onSelectedDate(selectedDate)
            }
        )
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onClearDate: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val formattedDate =
                String.format("%02d/%02d/%d", selectedMonth + 1, selectedDayOfMonth, selectedYear)
            onDateSelected(formattedDate)
        }, year, month, day
    )


    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() },
        enabled = false,
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Black,
            disabledLabelColor = Color.Black,
            disabledIndicatorColor = Color.Black
        ),
        trailingIcon = {
            if (selectedDate.isNotEmpty()) {
                IconButton(onClick = { onClearDate() }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear Date")
                }
            }
        }
    )


}

@Preview
@Composable
fun TransactionsList(lisOfTransactions: List<TransactionModel> = emptyList()) {

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(5.dp)) {
        items(items = lisOfTransactions) { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}

//@Preview
@Composable
fun TransactionItem(transaction: TransactionModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .clickable { }
        .fillMaxWidth()
        .padding(3.dp),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 3.dp)
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = transaction.transactionTitle, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Categorie: ${transaction.categoryId}",
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic
                    )
                    Text(text = transaction.transactionDate, color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Text(
                        text = transaction.amount.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = if (transaction.transactionType == "Venit") Color(0xFF349938) else Color.Red
                    )
                }

            }
            if (expanded) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Descriere: ${transaction.transactionDescription}", modifier = Modifier
                        .padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(text = if (expanded) "Restrange" else "Extinde")
                }
            }
        }

    }
}

@Composable
fun FilterDialog(
    showDialog: MutableState<Boolean>,
    onFilterApply: (String, List<String>) -> Unit,
    selectedType: MutableState<String>,
    selectedCategories: MutableState<List<String>>
) {
    if (showDialog.value) {
        val density = LocalContext.current.resources.displayMetrics.density
        val screenHeightPx = LocalContext.current.resources.displayMetrics.heightPixels
        val dialogHeightDp = (screenHeightPx * 0.5 / density).dp

        var expanded by remember { mutableStateOf(false) }


        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Filter Transactions") },
            text = {
                Column(modifier = Modifier.height(dialogHeightDp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Tipul tranzactiei: ")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(selectedType.value)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            offset = DpOffset(150.dp, 0.dp)
                        ) {
                            DropdownMenuItem(onClick = {
                                selectedType.value = "Venit"
                                expanded = false
                            }, text = { Text(text = "Venit") })
                            DropdownMenuItem(
                                onClick = {
                                    selectedType.value = "Cheltuiala"
                                    expanded = false
                                },
                                text = { Text(text = "Cheltuiala") })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select Categories:")
                    LazyColumn {
                        val categories = listOf(
                            "Salariu",
                            "Cumparaturi",
                            "Factura telefon",
                            "Altceva",
                            "Service masina",
                            "Mancare petru caine",
                            "1000",
                            "Alta factura",
                            "Ceva",
                            "Altceva"
                        )
                        items(categories) { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val currentCategories =
                                            selectedCategories.value.toMutableList()
                                        if (currentCategories.contains(category)) {
                                            currentCategories.remove(category)
                                        } else {
                                            currentCategories.add(category)
                                        }
                                        selectedCategories.value = currentCategories
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                Checkbox(
                                    checked = selectedCategories.value.contains(category),
                                    onCheckedChange = {
                                        val currentCategories =
                                            selectedCategories.value.toMutableList()
                                        if (it) {
                                            currentCategories.add(category)
                                        } else {
                                            currentCategories.remove(category)
                                        }
                                        selectedCategories.value = currentCategories
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = category)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onFilterApply(selectedType.value, selectedCategories.value)
                        showDialog.value = false
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}