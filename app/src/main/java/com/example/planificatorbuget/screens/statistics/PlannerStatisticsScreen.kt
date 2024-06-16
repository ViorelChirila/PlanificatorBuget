package com.example.planificatorbuget.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.FinancialFlux
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.components.SummaryChartCard
import com.example.planificatorbuget.components.BudgetEvolution
import com.example.planificatorbuget.components.PieChartCard
import com.example.planificatorbuget.model.TransactionModelParcelable
import com.example.planificatorbuget.navigation.PlannerScreens
import com.example.planificatorbuget.screens.SharedViewModel
import com.example.planificatorbuget.screens.categories.CategoriesScreenViewModel
import com.example.planificatorbuget.screens.transactions.TransactionsScreenViewModel
import com.example.planificatorbuget.utils.gradientBackgroundBrush
import com.google.gson.Gson

@Composable
fun PlannerStatisticsScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: TransactionsScreenViewModel = hiltViewModel(),
    categoriesScreenViewModel: CategoriesScreenViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val transactionsData by viewModel.transactions.collectAsState()
    val categoriesData by categoriesScreenViewModel.categories.collectAsState()
    val listOfTransactions by remember {
        derivedStateOf {
            transactionsData.data ?: emptyList()
        }
    }

    val listOfCategories by remember {
        derivedStateOf {
            categoriesData.data ?: emptyList()
        }
    }
    val options = listOf("Cheltuieli", "Venituri")
    val selectedOption by remember { mutableStateOf(options[0]) }
    val meanValue by remember { mutableDoubleStateOf(0.0) }

    val dataOrException by sharedViewModel.data.observeAsState()
    val user = dataOrException?.data
    val isLoading = dataOrException?.isLoading ?: true
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
                    title = "Statistici",
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
            Box(modifier = Modifier.padding(paddingValues)) {
                if (transactionsData.isLoading == true || isLoading || categoriesData.isLoading == true) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (listOfTransactions.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text ="Nu exista tranzactii pentru a afisa statistici.")
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        SummaryChartCard(listOfTransactions, selectedOption, meanValue, options) {
                            val parcelableList =
                                TransactionModelParcelable.fromTransactionModelList(
                                    listOfTransactions
                                )
                            val itemListJson = Gson().toJson(parcelableList)
                            navController.navigate(PlannerScreens.DailySummaryDetailedChartScreen.name + "/$itemListJson")
                        }
                        FinancialFlux(listOfTransactions) {
                            val parcelableList =
                                TransactionModelParcelable.fromTransactionModelList(
                                    listOfTransactions
                                )
                            val itemListJson = Gson().toJson(parcelableList)
                            navController.navigate(PlannerScreens.FinancialFluxDetailedChartScreen.name + "/$itemListJson")
                        }
                        BudgetEvolution(
                            listOfTransactions = listOfTransactions,
                            initialBudget = user!!.initialBudget
                        ){
                            val parcelableList =
                                TransactionModelParcelable.fromTransactionModelList(
                                    listOfTransactions
                                )
                            val itemListJson = Gson().toJson(parcelableList)
                            navController.navigate(PlannerScreens.BudgetEvolutionDetailedChartScreen.name + "/$itemListJson/${user.initialBudget.toFloat()}")
                        }
                        PieChartCard(
                            listOfTransactions = listOfTransactions,
                            listOfCategories = listOfCategories,
                            selectedOption = selectedOption,
                            options = options
                        )
                    }

                }
            }
        }
    }
}