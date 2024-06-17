package com.example.planificatorbuget.screens.transactions

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.planificatorbuget.components.AddTransactionDialog
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.FABContent
import com.example.planificatorbuget.components.FilterAndSortTransactions
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.components.SearchTransactionsByDateForm
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.navigation.PlannerScreens
import com.example.planificatorbuget.screens.SharedViewModel
import com.example.planificatorbuget.screens.categories.CategoriesScreenViewModel
import com.example.planificatorbuget.utils.exportTransactionsToCsv
import com.example.planificatorbuget.utils.formatTimestampToString
import com.example.planificatorbuget.utils.gradientBackgroundBrush
import com.example.planificatorbuget.utils.mapTransactionsToCategories

enum class SortOrder {
    NONE, ASCENDING, DESCENDING
}


@Composable
fun PlannerTransactionsScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: TransactionsScreenViewModel = hiltViewModel(),
    categoriesSharedViewModel: CategoriesScreenViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel = hiltViewModel(),
    textRecognitionViewModel: TextRecognitionViewModel = hiltViewModel(),
    selectedDate: String
) {
    val context = LocalContext.current
    Log.d("PlannerTransactionsScreen", "selectedDate: $selectedDate")
    val dataOrException by sharedViewModel.data.observeAsState()
    val isUpdateDone by sharedViewModel.isUpdateDone.observeAsState(initial = true)
    val user = dataOrException?.data

    val transactionsData by viewModel.transactions.collectAsState()
    val categoriesData by categoriesSharedViewModel.categories.collectAsState()

    val listOfCategories by remember {
        derivedStateOf {
            categoriesData.data ?: emptyList()
        }
    }

    val originalListOfTransactions by remember {
        derivedStateOf {
            transactionsData.data?.sortedByDescending { it.transactionDate.toDate() } ?: emptyList()
        }
    }
    val filteredListOfTransactions = remember { mutableStateOf(originalListOfTransactions) }
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val showLoading = rememberSaveable { mutableStateOf(false) }

    var sortOrder by remember { mutableStateOf(SortOrder.NONE) }

    val resultForAdd by viewModel.transactionAddResult.observeAsState()
    val resultForAddRecurring by viewModel.recurringTransactionAddResult.observeAsState()

    fun sortTransactions() {
        sortOrder = when (sortOrder) {
            SortOrder.NONE -> SortOrder.ASCENDING
            SortOrder.ASCENDING -> SortOrder.DESCENDING
            SortOrder.DESCENDING -> SortOrder.NONE
        }

        filteredListOfTransactions.value = when (sortOrder) {
            SortOrder.NONE -> originalListOfTransactions
            SortOrder.ASCENDING -> filteredListOfTransactions.value.sortedBy { it.amount }
            SortOrder.DESCENDING -> filteredListOfTransactions.value.sortedByDescending { it.amount }
        }
    }
    LaunchedEffect(originalListOfTransactions) {
        filteredListOfTransactions.value = originalListOfTransactions
    }

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
                    showDialog.value = true
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
                if (transactionsData.isLoading == true || categoriesData.isLoading == true) {
                    Log.d(
                        "isDataLoading",
                        "isDataLoading: ${transactionsData.isLoading.toString()}"
                    )
                    Log.d(
                        "isDataLoading",
                        "isDataLoading: ${categoriesData.isLoading.toString()}"
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {

                    Log.d(
                        "PlannerTransactionsScreen",
                        "PlannerTransactionsScreen: ${transactionsData.data.toString()}"
                    )
                    Log.d(
                        "PlannerTransactionsScreen",
                        "PlannerTransactionsScreen: ${categoriesData.data.toString()}"
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        SearchTransactionsByDateForm(selectedDate,
                            onImportTransactions = {navController.navigate(PlannerScreens.CsvUploadScreen.name)},
                            onExportTransactions = {
                                val transactionsWithCategories = mapTransactionsToCategories(
                                    originalListOfTransactions,
                                    listOfCategories
                                )

                                val fileName = "transactions.csv"
                                val uri = exportTransactionsToCsv(context, transactionsWithCategories, fileName)
                                if (uri != null) {
                                    Toast.makeText(context, "CSV exported to $uri", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error exporting CSV", Toast.LENGTH_SHORT).show()
                                }
                            }) { date ->
                            if (date.isEmpty())
                                filteredListOfTransactions.value = originalListOfTransactions
                            else
                                filteredListOfTransactions.value =
                                    originalListOfTransactions.filter {
                                        formatTimestampToString(it.transactionDate) == date
                                    }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(
                            color = Color.Black.copy(alpha = 0.3f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        FilterAndSortTransactions(
                            categories = listOfCategories,
                            onSort = {
                                sortTransactions()
                            },
                            onFilter = { type, categories ->
                                filteredListOfTransactions.value =
                                    originalListOfTransactions.filter {
                                        (type.isEmpty() || it.transactionType == type) &&
                                                (categories.isEmpty() || categories.contains(it.categoryId))
                                    }
                            }) { query ->
                            if (query.isEmpty()) {
                                filteredListOfTransactions.value = originalListOfTransactions
                            } else {
                                filteredListOfTransactions.value =
                                    originalListOfTransactions.filter {
                                        it.transactionTitle.contains(query, ignoreCase = true)
                                    }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(
                            color = Color.Black.copy(alpha = 0.3f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        TransactionsList(
                            lisOfTransactions = filteredListOfTransactions.value,
                            category = listOfCategories,
                            navController
                        )
                    }
                }
            }

        }

    }
    AddTransactionDialog(
        textRecognitionViewModel = textRecognitionViewModel,
        showDialog = showDialog,
        showLoading = showLoading,
        navController = navController,
        categories = listOfCategories,
        onAddTransaction = {transactionModel ->
            transactionModel.budgetSnapshot = user?.currentBudget ?: 0.0
            viewModel.addTransaction(transactionModel)
            val budget = user?.currentBudget?.plus(transactionModel.amount)
            sharedViewModel.updateBudget(budget!!)
            if (isUpdateDone) {
                sharedViewModel.fetchUser()
                sharedViewModel.resetUpdateStatus()
            }
            Log.d("PlannerTransactionsScreen", resultForAdd.toString())},
        onAddRecurringTransaction = { recurringTransactionModel ->
            viewModel.addRecurringTransaction(recurringTransactionModel)
        }
    )
    LaunchedEffect(resultForAdd) {
        when (resultForAdd) {
            is Response.Loading -> {
                showLoading.value = true
                Toast.makeText(context, "Se adauga tranzactia...", Toast.LENGTH_SHORT).show()
            }

            is Response.Success -> {
                if ((resultForAdd as Response.Success).data == true) {
                    Toast.makeText(context, "Tranzactie adaugata cu succes", Toast.LENGTH_SHORT)
                        .show()
                    showLoading.value = false
                    viewModel.fetchTransactionsFromDatabase()
                }
            }

            is Response.Error -> {
                Toast.makeText(
                    context,
                    (resultForAdd as Response.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> { /* no-op */
            }
        }
    }
    LaunchedEffect(resultForAddRecurring) {
        when (resultForAddRecurring) {
            is Response.Loading -> {
                showLoading.value = true
                Toast.makeText(context, "Se adauga tranzactia...", Toast.LENGTH_SHORT).show()
            }

            is Response.Success -> {
                if ((resultForAddRecurring as Response.Success).data == true) {
                    Toast.makeText(context, "Tranzactie adaugata cu succes", Toast.LENGTH_SHORT)
                        .show()
                    showLoading.value = false
                }
            }

            is Response.Error -> {
                Toast.makeText(
                    context,
                    (resultForAddRecurring as Response.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> { /* no-op */
            }
        }

    }
}

@Composable
fun TransactionsList(
    lisOfTransactions: List<TransactionModel> = emptyList(),
    category: List<TransactionCategoriesModel>,
    navController: NavController
) {

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(5.dp)) {
        items(items = lisOfTransactions) { transaction ->
            val categoryItem = category.find { it.categoryId == transaction.categoryId }
            TransactionItem(transaction = transaction, category = categoryItem,navController)
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionModel, category: TransactionCategoriesModel?,navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .clickable { navController.navigate(PlannerScreens.TransactionDetailsScreen.name+"/${transaction.transactionId}")}
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
                Surface(shape = CircleShape) {
                    Log.d("CategoryItem", "categoryIcon: ${category?.categoryIcon}")
                    AsyncImage(
                        model = category?.categoryIcon?.toUri(),
                        contentDescription = "icon",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(60.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.transactionTitle,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Categorie: ${category?.categoryName}",
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatTimestampToString(transaction.transactionDate,pattern = "MM/dd/yyyy HH:mm"),
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    val transactionColor =
                        if (transaction.transactionType == "Venit") Color(0xFF349938) else Color.Red
                    Text(
                        text = if (transaction.transactionType == "Venit") "+${transaction.amount}" + " LEI" else transaction.amount.toString() + " LEI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = transactionColor
                    )
                    Text(
                        text = "Sold precedent:\n${transaction.budgetSnapshot} LEI",
                        color = transactionColor,
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        maxLines = 2,
                        lineHeight = 12.sp
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