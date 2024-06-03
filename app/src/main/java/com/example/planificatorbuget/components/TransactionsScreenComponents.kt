package com.example.planificatorbuget.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.planificatorbuget.R
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.navigation.PlannerScreens
import java.util.Calendar

@Composable
fun FilterAndSortTransactions(
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
            title = { Text("Filtreaza tranzactiile") },
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
                    Text("Alege categoriile:")
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
                    Text("Salveaza")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Renunta")
                }
            }
        )
    }
}

@Composable
fun SearchTransactionsByDateForm(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    navController: NavController,
    showDialog: MutableState<Boolean>,
    showLoading: MutableState<Boolean>,
    categories: MutableState<List<TransactionCategoriesModel>> = mutableStateOf(listOf()),
    onAddTransaction: (TransactionModel) -> Unit = {}
) {

    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background,
            ) {
                if (showLoading.value) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }else {
                    Column(modifier = Modifier.padding(16.dp)) {
                        var title by remember { mutableStateOf("") }
                        var amount by remember { mutableStateOf("") }
                        var type by remember { mutableStateOf("") }
                        var category by remember { mutableStateOf("") }
                        var date by remember { mutableStateOf("") }
                        var description by remember { mutableStateOf("") }

                        var expandedType by remember { mutableStateOf(false) }
                        var extendedCategory by remember { mutableStateOf(false) }

                        val valid = remember(title, amount, type, category, date, description) {
                            title.trim().isNotEmpty() && amount.trim().isNotEmpty() && type.trim()
                                .isNotEmpty() && category.trim().isNotEmpty() && date.trim()
                                .isNotEmpty() && description.trim().isNotEmpty()
                        }

                        val context = LocalContext.current

                        Text(
                            text = "Add New Transaction",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Titlu") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Valoare in LEI") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                if (type == "Venit") {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Plus sign",
                                        tint = Color(0xFF349938)
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_remove_24),
                                        contentDescription = "Minus sign",
                                        tint = Color.Red
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedType,
                            onExpandedChange = { expandedType = !expandedType }) {
                            OutlinedTextField(
                                value = type,
                                onValueChange = { type = it },
                                label = { Text(text = "Tip (Venit/Cheltuiala)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown Arrow"
                                    )
                                },
                                readOnly = true
                            )
                            ExposedDropdownMenu(
                                expanded = expandedType,
                                onDismissRequest = { expandedType = false },
                            ) {
                                DropdownMenuItem(onClick = {
                                    type = "Venit"
                                    expandedType = false
                                }, text = { Text("Venit") })


                                DropdownMenuItem(onClick = {
                                    type = "Cheltuiala"
                                    expandedType = false
                                }, text = { Text("Cheltuiala") })

                            }

                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = extendedCategory,
                            onExpandedChange = { extendedCategory = !extendedCategory }) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text(text = "Categorie") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown Arrow"
                                    )
                                },
                                readOnly = true
                            )
                            ExposedDropdownMenu(
                                expanded = extendedCategory,
                                onDismissRequest = { extendedCategory = false },
                                modifier = Modifier.heightIn(max = 200.dp)
                            ) {
                                categories.value.forEach { item ->
                                    DropdownMenuItem(onClick = {
                                        category = item.categoryId
                                        extendedCategory = false
                                    }, text = { Text(text=item.categoryName) })
                                }
                                DropdownMenuItem(text = { Text(text = "Adauga categorie noua")}, onClick = {
                                    navController.navigate(PlannerScreens.CategoriesScreen.name)
                                })

                            }

                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        DatePickerField(
                            label = "Data tranzactiei",
                            selectedDate = date,
                            onDateSelected = { date = it },
                            onClearDate = { date = "" }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descriere") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp), // Set a minimum height to allow multiple lines
                            maxLines = 5,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        // Add photo functionality
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Add Photo",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDialog.value = false }) {
                                Text("Renunta")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {

                                    if (valid) {
                                        val tempAmount = if (type == "Venit") amount.toDouble() else amount.toDouble() * -1
                                        val transaction = TransactionModel(
                                            amount = tempAmount,
                                            transactionType = type,
                                            transactionDate = date,
                                            transactionTitle = title,
                                            transactionDescription = description,
                                        )
                                        onAddTransaction(transaction)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Toate campurile sunt obligatorii",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            ) {
                                Text("Adauga")
                            }
                        }
                    }
                }
            }
        }
    }
}