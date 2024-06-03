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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AddTransactionDialog
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.FABContent
import com.example.planificatorbuget.components.FilterAndSortTransactions
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.components.SearchTransactionsByDateForm
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.utils.gradientBackgroundBrush

enum class SortOrder {
    NONE, ASCENDING, DESCENDING
}

@Preview
@Composable
fun PlannerTransactionsScreen(navController: NavController = NavController(LocalContext.current), viewModel: TransactionsScreenViewModel = hiltViewModel()) {

    val transactionsData by viewModel.transactions.collectAsState()
    val originalListOfTransactions by remember { derivedStateOf { transactionsData.data ?: emptyList() } }
    val filteredListOfTransactions = remember { mutableStateOf(originalListOfTransactions) }
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val showLoading = rememberSaveable { mutableStateOf(false) }

    var sortOrder by remember { mutableStateOf(SortOrder.NONE) }

    val resultForAdd by viewModel.transactionAddResult.observeAsState()
    val context = LocalContext.current

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
                colors = if(!isSystemInDarkTheme()) listOf(
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
                if (transactionsData.isLoading == true){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }else {

                    Log.d("PlannerTransactionsScreen", "PlannerTransactionsScreen: ${transactionsData.data.toString()}")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        SearchTransactionsByDateForm { date ->
                            if (date.isEmpty())
                                filteredListOfTransactions.value = originalListOfTransactions
                            else
                                filteredListOfTransactions.value =
                                    originalListOfTransactions.filter {
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
                        TransactionsList(lisOfTransactions = filteredListOfTransactions.value)
                    }
                }
            }

        }

    }
    AddTransactionDialog(showDialog = showDialog,showLoading = showLoading, navController = navController){ transactionModel->
        viewModel.addTransaction(transactionModel)
        Log.d("PlannerTransactionsScreen", resultForAdd.toString())
    }
    if (resultForAdd is Response.Loading){
        showLoading.value = true
        Toast.makeText(context, "Se adauga tranzactia...", Toast.LENGTH_SHORT).show()
    }
    else if (resultForAdd is Response.Success && (resultForAdd as Response.Success).data == true){
        Toast.makeText(context, "Tranzactie adaugata cu succes", Toast.LENGTH_SHORT).show()
        showLoading.value = false
    }
    else if (resultForAdd is Response.Error){
        Toast.makeText(context, (resultForAdd as Response.Error).message, Toast.LENGTH_SHORT).show()
    }
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
                        text = if(transaction.transactionType == "Venit") "+${transaction.amount}" else transaction.amount.toString(),
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