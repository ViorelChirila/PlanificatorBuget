package com.example.planificatorbuget.screens.chartdetailsscreeens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.BudgetEvolutionLineChart
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.model.TransactionModelParcelable
import com.example.planificatorbuget.utils.calculateCumulativeBudget

@Composable
fun BudgetEvolutionDetailedChartScreen(
    navController: NavController = NavController(LocalContext.current),
    transactions: List<TransactionModelParcelable> = emptyList(),
    initialBudget: Double = 0.0
){
    val transactionList: List<TransactionModel> =
        TransactionModelParcelable.toTransactionModel(transactions)
    val chartData = calculateCumulativeBudget(transactionList, initialBudget)

    Surface {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Sumar detaliat",
                    haveNotifications = false,
                    isHomeScreen = false,
                    navController = navController,
                    color = Color.Black
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Surface(
                        modifier = Modifier
                            .height(400.dp)
                            .fillMaxWidth(), color = Color.White
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            BudgetEvolutionLineChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp),
                                chartData = chartData,
                                xAxisRotationAngle = 0f,
                                xAxisLabelFontSize = 10.sp,
                                lineStyleColor = Color(0x9E4330AA),
                                shadowUnderLine = Color(0x9E4330AA),
                                intersectionPointRadius = 4.dp
                            )
                        }
                    }
                }
            }
        }
    }
}