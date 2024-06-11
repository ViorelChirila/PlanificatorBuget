package com.example.planificatorbuget.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.components.SummaryChartCard
import com.example.planificatorbuget.screens.transactions.TransactionsScreenViewModel
import com.example.planificatorbuget.utils.gradientBackgroundBrush

@Composable
fun PlannerStatisticsScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: TransactionsScreenViewModel = hiltViewModel()
) {
    val transactionsData by viewModel.transactions.collectAsState()
    val listOfTransactions by remember {
        derivedStateOf {
            transactionsData.data ?: emptyList()
        }
    }
    val options = listOf("Cheltuieli", "Venituri")
    val selectedOption by remember { mutableStateOf(options[0]) }
    val meanValue by remember { mutableDoubleStateOf(0.0) }
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
                if (transactionsData.isLoading == true) {
                    CircularProgressIndicator()
                } else {

                    SummaryChartCard(listOfTransactions, selectedOption, meanValue, options)

                }
            }
        }
    }
}