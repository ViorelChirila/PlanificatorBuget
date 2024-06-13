package com.example.planificatorbuget.screens.recurringtransactions

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.DatePickerField
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.RecurringTransactionModel
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.utils.formatStringToTimestamp
import com.example.planificatorbuget.utils.formatTimestampToString
import com.example.planificatorbuget.utils.gradientBackgroundBrush
import com.google.firebase.Timestamp
import java.util.Calendar

@Preview
@Composable
fun PlannerRecurringTransactionsScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: RecurringTransactionsScreenViewModel = hiltViewModel()
) {
    val recurringTransactions by viewModel.recurringTransactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val resultForUpdate by viewModel.recurringTransactionUpdateResult.observeAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier.background(
            brush = gradientBackgroundBrush(
                isVerticalGradient = true,
                colors = if (!isSystemInDarkTheme()) listOf(
                    Color(0xFF7F9191),
                    Color(0xffc3c3d8),
                    Color(0xff00d4ff)
                ) else listOf(
                    Color(0xFF332D2D),
                    Color(0xFF232D52),
                    Color(0xFF1442A0)
                )
            )
        )
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Cont",
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
            containerColor = Color.Transparent
        ) { paddingValues ->

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Trazactii recurente active",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(
                                top = 15.dp,
                                start = 15.dp,
                                end = 15.dp,
                                bottom = 10.dp
                            )
                        )
                        HorizontalDivider()

                        LazyRow(contentPadding = PaddingValues(5.dp)) {
                            items(items = recurringTransactions) { recurringTransaction ->
                                if (recurringTransaction.status == "activ") {
                                    Box(modifier = Modifier.padding(5.dp)) {
                                        RecurringTransactionsItem(
                                            recurringTransaction,
                                            categories[recurringTransaction.transactionId!!]!!,
                                            onDelete = { transactionId ->
                                                viewModel.deleteRecurringTransaction(transactionId)
                                            },
                                            onUpdateStatus = { transactionId, status ->
                                                viewModel.updateRecurrentTransactionStatus(transactionId, status)
                                            }
                                        ) { startDate, endDate, recurrenceInterval ->
                                            viewModel.updateRecurringTransaction(
                                                recurringTransaction.transactionId!!,
                                                startDate,
                                                endDate,
                                                recurrenceInterval
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Trazactii recurente inactive",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(
                                top = 15.dp,
                                start = 15.dp,
                                end = 15.dp,
                                bottom = 10.dp
                            )
                        )
                        HorizontalDivider()
                        LazyRow(contentPadding = PaddingValues(5.dp)) {
                            items(items = recurringTransactions) { recurringTransaction ->
                                if (recurringTransaction.status == "inactiv") {
                                    Box(modifier = Modifier.padding(5.dp)) {
                                        RecurringTransactionsItem(
                                            recurringTransaction,
                                            categories[recurringTransaction.transactionId!!]!!,
                                            onDelete = { transactionId ->
                                                viewModel.deleteRecurringTransaction(transactionId)
                                            },
                                            onUpdateStatus = { transactionId, status ->
                                                viewModel.updateRecurrentTransactionStatus(transactionId, status)
                                            }
                                        ) { startDate, endDate, recurrenceInterval ->
                                            viewModel.updateRecurringTransaction(
                                                recurringTransaction.transactionId!!,
                                                startDate,
                                                endDate,
                                                recurrenceInterval
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        LaunchedEffect(resultForUpdate) {
            when (resultForUpdate) {
                is Response.Loading -> {
                    Toast.makeText(context, "Se actualizeaza...", Toast.LENGTH_SHORT).show()
                }

                is Response.Success -> {
                    if ((resultForUpdate as Response.Success).data == true) {
                        Toast.makeText(context, "Actualizarea a fost facuta cu succes", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.fetchRecurringTransactionsFromDatabase()
                    }
                }

                is Response.Error -> {
                    Toast.makeText(
                        context,
                        (resultForUpdate as Response.Error).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> { /* no-op */
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTransactionsItem(
    recurringTransaction: RecurringTransactionModel,
    category: TransactionCategoriesModel,
    onDelete: (String) -> Unit = { _ -> },
    onUpdateStatus: (String, String) -> Unit = { _, _ -> },
    onEdit: (Timestamp, Timestamp, String) -> Unit = { _, _, _ -> },
) {
    var startDate by remember { mutableStateOf(formatTimestampToString(recurringTransaction.startDate)) }
    var endDate by remember { mutableStateOf(formatTimestampToString(recurringTransaction.endDate)) }
    var enable by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val currentDateInMillis = Calendar.getInstance().timeInMillis
    val startDateInMillis =
        formatStringToTimestamp(startDate, "MM/dd/yyyy")?.toDate()?.time ?: currentDateInMillis

    var expandedInterval by remember { mutableStateOf(false) }
    var recurrenceInterval by remember { mutableStateOf(recurringTransaction.recurrenceInterval) }
    var showDialog by remember { mutableStateOf(false) }
    val status by remember { mutableStateOf(recurringTransaction.status) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.width(350.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(shape = CircleShape) {
                    AsyncImage(
                        model = category.categoryIcon.toUri(),
                        contentDescription = "icon",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(60.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = recurringTransaction.transactionTitle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Text(
                        text = "Categorie: ${category.categoryName}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    val transactionColor =
                        if (recurringTransaction.transactionType == "Venit") Color(0xFF349938) else Color.Red
                    Text(
                        text = if (recurringTransaction.transactionType == "Venit") "+${recurringTransaction.amount}" + " Lei" else recurringTransaction.amount.toString() + " Lei",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 15.dp),
                        color = transactionColor
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = recurringTransaction.status,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        if (recurringTransaction.status == "inactiv")
                            Icon(
                                imageVector = Icons.Default.RemoveCircle,
                                contentDescription = "status icon",
                                tint = Color.Red
                            )
                        else if (recurringTransaction.status == "activ") {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "status icon",
                                tint = Color(0xFF349938)
                            )
                        }
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(start = 10.dp, end = 10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Data inceput:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                DatePickerField(
                    label = "Data de început",
                    enable = enable,
                    selectedDate = startDate,
                    onDateSelected = { selectedDate ->
                        val selectedDateInMillis =
                            formatStringToTimestamp(selectedDate, "MM/dd/yyyy")?.toDate()?.time
                        if (selectedDateInMillis != null && selectedDateInMillis >= currentDateInMillis) {
                            startDate = selectedDate
                        } else {
                            Toast.makeText(
                                context,
                                "Data de inceput trebuie sa fie dupa data curenta!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onClearDate = { startDate = "" }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Data sfarsit:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                DatePickerField(
                    label = "Data de sfarsit",
                    enable = enable,
                    selectedDate = endDate,
                    onDateSelected = { selectedDate ->
                        val selectedDateInMillis =
                            formatStringToTimestamp(selectedDate, "MM/dd/yyyy")?.toDate()?.time
                        if (selectedDateInMillis != null && selectedDateInMillis >= startDateInMillis) {
                            endDate = selectedDate
                        } else {
                            Toast.makeText(
                                context,
                                "Data de sfarsit trebuie sa fie dupa data de inceput!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onClearDate = { endDate = "" }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expandedInterval && enable,
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
                        readOnly = true,
                        enabled = enable
                    )
                    ExposedDropdownMenu(
                        expanded = expandedInterval && enable,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showDialog = true },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Sterge")
                }
                Button(
                    onClick = {
                        if (status == "activ") {
                            val newStatus = "inactiv"
                            onUpdateStatus(recurringTransaction.transactionId!!, newStatus)
                        }
                        else {
                            val newStatus = "activ"
                            onUpdateStatus(recurringTransaction.transactionId!!, newStatus)
                        }
                    },
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = if (status == "activ") "Dezactiveaza" else "Activeaza")
                }
                Button(
                    onClick = {
                        if (enable) {
                            val newStartDate = formatStringToTimestamp(startDate)
                            val newEndDate = formatStringToTimestamp(endDate)

                            if (newStartDate != null && newEndDate != null && (newStartDate != recurringTransaction.startDate || newEndDate != recurringTransaction.endDate || recurrenceInterval != recurringTransaction.recurrenceInterval)) {
                                onEdit(
                                    newStartDate, newEndDate, recurrenceInterval
                                )
                                Toast.makeText(
                                    context,
                                    "Schimbarile au fost salvate cu succes!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (newStartDate == null || newEndDate == null) {
                                startDate = formatTimestampToString(recurringTransaction.startDate)
                                endDate = formatTimestampToString(recurringTransaction.endDate)
                                Toast.makeText(context, "Date invalide", Toast.LENGTH_SHORT).show()
                            }
                        }
                        enable = !enable
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (enable) Color(0xFF349938) else Color.Blue
                    )
                ) {
                    Text(text = if (!enable) "Editeaza" else "Salveaza")
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmare Stergere") },
            text = { Text(text = "Ești sigur că vrei să ștergi această tranzacție recurentă?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(recurringTransaction.transactionId!!)
                        showDialog = false
                    }
                ) {
                    Text("Da")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Nu")
                }
            }
        )
    }
}
