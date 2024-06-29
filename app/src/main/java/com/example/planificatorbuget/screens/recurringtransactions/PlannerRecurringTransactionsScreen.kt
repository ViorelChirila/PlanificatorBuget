package com.example.planificatorbuget.screens.recurringtransactions

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.components.RecurringTransactionsItem
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.utils.gradientBackgroundBrush

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
                            text = "Trazacții recurente active",
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
                            text = "Trazacții recurente inactive",
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
                    Toast.makeText(context, "Se actualizează...", Toast.LENGTH_SHORT).show()
                }

                is Response.Success -> {
                    if ((resultForUpdate as Response.Success).data == true) {
                        Toast.makeText(context, "Actualizarea a fost facută cu succes", Toast.LENGTH_SHORT)
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

