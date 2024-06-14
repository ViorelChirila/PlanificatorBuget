package com.example.planificatorbuget.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import com.example.planificatorbuget.model.RecurringTransactionModel
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.navigation.PlannerScreens
import com.example.planificatorbuget.screens.transactions.TextRecognitionViewModel
import com.example.planificatorbuget.utils.formatStringToTimestamp
import com.example.planificatorbuget.utils.isDateBeforeToday
import com.example.planificatorbuget.utils.isDateInPast
import java.util.Calendar

@Composable
fun FilterAndSortTransactions(
    categories: List<TransactionCategoriesModel>,
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
        categories = categories,
        showDialog = showDialog,
        onFilterApply = onFilter,
        selectedType = selectedType,
        selectedCategories = selectedCategories
    )
}

@Composable
fun FilterDialog(
    showDialog: MutableState<Boolean>,
    categories: List<TransactionCategoriesModel>,
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
                        items(categories) { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val currentCategories =
                                            selectedCategories.value.toMutableList()
                                        if (currentCategories.contains(category.categoryId)) {
                                            currentCategories.remove(category.categoryId)
                                        } else {
                                            currentCategories.add(category.categoryId)
                                        }
                                        selectedCategories.value = currentCategories
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                Checkbox(
                                    checked = selectedCategories.value.contains(category.categoryId),
                                    onCheckedChange = {
                                        val currentCategories =
                                            selectedCategories.value.toMutableList()
                                        if (it) {
                                            currentCategories.add(category.categoryId)
                                        } else {
                                            currentCategories.remove(category.categoryId)
                                        }
                                        selectedCategories.value = currentCategories
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = category.categoryName)
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
    initialDate: String?,
    onSelectedDate: (String) -> Unit = { }
) {
    var selectedDate by remember { mutableStateOf(initialDate?.takeIf { it.isNotBlank() } ?: "") }

    // Trigger the search when the initial date is not null and not blank
    LaunchedEffect(initialDate) {
        initialDate?.takeIf { it.isNotBlank() }?.let {
            onSelectedDate(it)
        }
    }

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
fun DateTimePickerField(
    label: String,
    selectedDateTime: String,
    onDateTimeSelected: (String) -> Unit,
    onClearDateTime: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var useCurrentTime by remember { mutableStateOf(false) }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            if (useCurrentTime) {
                val formattedDateTime = String.format(
                    "%02d/%02d/%d %02d:%02d",
                    selectedMonth + 1, selectedDayOfMonth, selectedYear,
                    hour, minute
                )
                onDateTimeSelected(formattedDateTime)
            } else {
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                        val formattedDateTime = String.format(
                            "%02d/%02d/%d %02d:%02d",
                            selectedMonth + 1, selectedDayOfMonth, selectedYear,
                            selectedHour, selectedMinute
                        )
                        onDateTimeSelected(formattedDateTime)
                    }, hour, minute, true
                )
                timePickerDialog.show()
            }
        }, year, month, day
    )

    Column {
        OutlinedTextField(
            value = selectedDateTime,
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
                if (selectedDateTime.isNotEmpty()) {
                    IconButton(onClick = { onClearDateTime() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Date")
                    }
                }
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Checkbox(
                checked = useCurrentTime,
                onCheckedChange = { useCurrentTime = it }
            )
            Text(text = "Use current time")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    navController: NavController,
    textRecognitionViewModel: TextRecognitionViewModel,
    showDialog: MutableState<Boolean>,
    showLoading: MutableState<Boolean>,
    categories: List<TransactionCategoriesModel>,
    onAddTransaction: (TransactionModel) -> Unit ,
    onAddRecurringTransaction: (RecurringTransactionModel) -> Unit
) {

    if (showDialog.value) {
        Dialog(onDismissRequest = {
            showDialog.value = false
            textRecognitionViewModel.resetRecognizedText()
        }) {
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
                } else {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .height(640.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        var title by remember { mutableStateOf("") }
                        var amount by remember { mutableStateOf("") }
                        var type by remember { mutableStateOf("") }
                        var categoryId by remember { mutableStateOf("") }
                        var categoryName by remember { mutableStateOf("") }
                        var date by remember { mutableStateOf("") }
                        val description = remember { mutableStateOf("") }
                        var isRecurring by remember { mutableStateOf(false) }
                        var startDate by remember { mutableStateOf("") }
                        var endDate by remember { mutableStateOf("") }
                        var recurrenceInterval by remember { mutableStateOf("") }
                        var imageSelectedUri by remember { mutableStateOf<Uri?>(null) }

                        var expandedType by remember { mutableStateOf(false) }
                        var extendedCategory by remember { mutableStateOf(false) }
                        var expandedInterval by remember { mutableStateOf(false) }

                        val valid =
                            remember(title, amount, type, categoryId, date, description.value) {
                                title.trim().isNotEmpty() && amount.trim()
                                    .isNotEmpty() && type.trim()
                                    .isNotEmpty() && categoryId.trim().isNotEmpty() && date.trim()
                                    .isNotEmpty() && description.value.trim().isNotEmpty()
                            }

                        val validRecurring = remember(
                            title,
                            amount,
                            type,
                            categoryId,
                            description.value,
                            startDate,
                            endDate,
                            recurrenceInterval
                        ) {
                            title.trim().isNotEmpty() && amount.trim().isNotEmpty() && type.trim()
                                .isNotEmpty() && categoryId.trim()
                                .isNotEmpty() && description.value.trim()
                                .isNotEmpty() && startDate.trim()
                                .isNotEmpty() && recurrenceInterval.trim().isNotEmpty()
                        }

                        val context = LocalContext.current

                        Text(
                            text = "Adauga tranzactie noua",
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
                            singleLine = true,
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
                                value = categoryName,
                                onValueChange = { /* No-op */ },
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
                                categories.forEach { item ->
                                    DropdownMenuItem(onClick = {
                                        categoryId = item.categoryId
                                        categoryName = item.categoryName
                                        extendedCategory = false
                                    }, text = { Text(text = item.categoryName) })
                                }
                                DropdownMenuItem(
                                    text = { Text(text = "Adauga categorie noua") },
                                    onClick = {
                                        navController.navigate(PlannerScreens.CategoriesScreen.name)
                                    })

                            }

                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (!isRecurring) {
                            DateTimePickerField(
                                label = "Data tranzactiei",
                                selectedDateTime = date,
                                onDateTimeSelected = { date = it },
                                onClearDateTime = { date = "" }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        DescriptionInputField(description, textRecognitionViewModel)
                        {
                            imageSelectedUri = it
                        }
                        Log.d("description", description.value)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isRecurring,
                                onCheckedChange = { isChecked ->
                                    isRecurring = isChecked
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tranzactie recurentă")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isRecurring) {
                            DatePickerField(
                                label = "Data de început",
                                selectedDate = startDate,
                                onDateSelected = { startDate = it },
                                onClearDate = { startDate = "" }
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            DatePickerField(
                                label = "Data de sfârșit",
                                selectedDate = endDate,
                                onDateSelected = { endDate = it },
                                onClearDate = { endDate = "" }
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            ExposedDropdownMenuBox(
                                expanded = expandedInterval,
                                onExpandedChange = { expandedInterval = !expandedInterval }) {
                                OutlinedTextField(
                                    value = recurrenceInterval,
                                    onValueChange = { /* No-op */ },
                                    label = { Text("Interval recurență") },
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
                                    expanded = expandedInterval,
                                    onDismissRequest = { expandedInterval = false }
                                ) {
                                    DropdownMenuItem(onClick = {
                                        recurrenceInterval = "Zilnic"
                                        expandedInterval = false
                                    }, text = { Text("Zilnic") })

                                    DropdownMenuItem(onClick = {
                                        recurrenceInterval = "Săptămânal"
                                        expandedInterval = false
                                    }, text = { Text("Săptămânal") })

                                    DropdownMenuItem(onClick = {
                                        recurrenceInterval = "Lunar"
                                        expandedInterval = false
                                    }, text = { Text("Lunar") })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                showDialog.value = false
                                textRecognitionViewModel.resetRecognizedText()
                            }) {
                                Text("Renunta")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {

                                    if (!isRecurring) {
                                        if (valid) {
                                            val tempAmount =
                                                if (type == "Venit") amount.toDouble() else amount.toDouble() * -1
                                            val transaction = TransactionModel(
                                                amount = tempAmount,
                                                transactionType = type,
                                                transactionDate = formatStringToTimestamp(
                                                    date,
                                                    pattern = "MM/dd/yyyy HH:mm"
                                                )!!,
                                                transactionTitle = title,
                                                transactionDescription = description.value,
                                                categoryId = categoryId,
                                                descriptionImage = imageSelectedUri.toString()
                                            )
                                            onAddTransaction(transaction)
                                            description.value = ""
                                            textRecognitionViewModel.resetRecognizedText()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Toate campurile sunt obligatorii",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        if (validRecurring) {
                                            if (isDateInPast(startDate)) {
                                                Toast.makeText(
                                                    context,
                                                    "Data de inceput trebuie sa fie in viitor",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@Button
                                            }
                                            if (isDateBeforeToday(endDate)) {
                                                Toast.makeText(
                                                    context,
                                                    "Data de sfarsit trebuie sa fie in viitor",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@Button
                                            }
                                            val tempAmount =
                                                if (type == "Venit") amount.toDouble() else amount.toDouble() * -1
                                            val recurringTransaction = RecurringTransactionModel(
                                                amount = tempAmount,
                                                transactionType = type,
                                                transactionTitle = title,
                                                transactionDescription = description.value,
                                                categoryId = categoryId,
                                                startDate = formatStringToTimestamp(startDate)!!,
                                                endDate = formatStringToTimestamp(endDate)!!,
                                                recurrenceInterval = recurrenceInterval
                                            )
                                            onAddRecurringTransaction(recurringTransaction)
                                            description.value = ""
                                            textRecognitionViewModel.resetRecognizedText()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Toate campurile sunt obligatorii",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
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

@Composable
fun DescriptionInputField(
    description: MutableState<String>,
    viewModel: TextRecognitionViewModel,
    onImageSelected: (Uri) -> Unit = {}
) {
    val text by viewModel.recognizedText.collectAsState()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onImageSelected(uri)
                viewModel.resetRecognizedText()
                description.value = ""
                viewModel.recognizeText(uri)
            }
        }
    )

    LaunchedEffect(text) {
        if (text.isNotEmpty() && !description.value.contains(text)) {
            description.value += " $text"
        }
        Log.d("Text", text)
    }

    OutlinedTextField(
        value = description.value,
        onValueChange = { description.value = it },
        label = { Text("Descriere") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        maxLines = 5,
        trailingIcon = {
            IconButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
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
}