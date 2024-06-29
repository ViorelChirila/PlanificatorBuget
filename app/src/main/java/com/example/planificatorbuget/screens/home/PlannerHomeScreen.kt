package com.example.planificatorbuget.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.navigation.PlannerScreens
import com.example.planificatorbuget.screens.SharedViewModel
import com.example.planificatorbuget.utils.convertMillisToDate
import com.example.planificatorbuget.utils.formatTimestampToString
import com.example.planificatorbuget.utils.gradientBackgroundBrush
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PlannerHomeScreen(
    navController: NavController = NavController(LocalContext.current),
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    viewModel: SharedViewModel = hiltViewModel()
) {

    val dataOrException by viewModel.data.observeAsState()
    val transactionsData by homeScreenViewModel.transactions.collectAsState()
    val user = dataOrException?.data
    val isLoading = dataOrException?.isLoading ?: true
    val listOfTransactions by remember {
        derivedStateOf {
            transactionsData.data?.sortedByDescending { it.transactionDate.toDate() } ?: emptyList()
        }
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
                    title = "Acasă",
                    haveNotifications = false,
                    isHomeScreen = true,
                    navController = navController
                )
            },
            bottomBar = {
                NavigationBarComponent(navController = navController)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (isLoading || transactionsData.isLoading == true) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val (totalIncome, totalExpenses) = if (listOfTransactions.isNotEmpty()) calculateTotals(listOfTransactions) else Pair(0.0, 0.0)
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Card(
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 10.dp)
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                            ),
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${user?.currentBudget} Lei",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White,
                                ),
                                elevation = CardDefaults.cardElevation(5.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row {
                                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Arrow Upward", tint = Color(0xFF158A15))
                                    }
                                    Text(
                                        text = "Venituri",
                                        color = Color(0xFF158A15),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "+$totalIncome Lei",
                                        color = Color(0xFF158A15)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White,
                                ),
                                elevation = CardDefaults.cardElevation(5.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row {
                                        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Arrow Upward", tint = Color.Red)
                                    }
                                    Text(
                                        text = "Cheltuieli",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$totalExpenses Lei",
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                        Card(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val state =
                                    rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
                                DatePicker(
                                    state = state, modifier = Modifier.padding(8.dp),
                                    title = null,
                                    headline = {
                                        Text(
                                            text = "Alege o dată",
                                            modifier = Modifier.padding(start = 10.dp)
                                        )
                                    },
                                )

                                val formattedDate = state.selectedDateMillis?.let { millis ->
                                    convertMillisToDate(millis)
                                } ?: ""
                                Text(
                                    text = "Data selectată: $formattedDate",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp), horizontalArrangement = Arrangement.End
                                ) {
                                    Button(onClick = {
                                        val encodedDate = URLEncoder.encode(
                                            formattedDate,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("${PlannerScreens.TransactionsScreen.name}/${encodedDate}")
                                    }) {
                                        Text(text = "Detalii")
                                    }
                                }
                            }
                        }
                        Card(
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 10.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                            ),
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Ultimele 3 tranzacții",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(15.dp)
                                )
                                HorizontalDivider( modifier = Modifier.padding(start = 15.dp, end = 15.dp))
                                if (listOfTransactions.size < 3) {
                                    Text(
                                        text = "Nu există tranzacții",
                                        modifier = Modifier.padding(15.dp)
                                    )
                                }
                                else {
                                    TransactionCard(transaction = listOfTransactions[0])
                                    TransactionCard(transaction = listOfTransactions[1])
                                    TransactionCard(transaction = listOfTransactions[2])
                                }

                            }

                        }

                    }

                }

            }
        }
    }
}


@Composable
fun TransactionCard(transaction: TransactionModel) {
    Card(
        modifier = Modifier
            .padding(start= 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFECF7F5),
        ),
        border = BorderStroke(1.dp, if (transaction.transactionType=="Venit")Color(0xFF158A15)else Color.Red),

    ) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.transactionTitle,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatTimestampToString(transaction.transactionDate),
                    color = Color.Gray
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${transaction.amount} Lei",
                    color = when (transaction.transactionType) {
                        "Venit" -> Color(0xFF158A15)
                        "Cheltuiala" -> Color.Red
                        else -> Color.Black
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun calculateTotals(transactions: List<TransactionModel>): Pair<Double, Double> {
    var totalIncome = 0.0
    var totalExpenses = 0.0

    transactions.forEach { transaction ->
        when (transaction.transactionType) {
            "Venit" -> totalIncome += transaction.amount
            "Cheltuiala" -> totalExpenses += transaction.amount
        }
    }

    return Pair(totalIncome, totalExpenses)
}