package com.example.planificatorbuget.screens.chartdetailsscreeens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.CustomDropdownMenuForPeriodSelection
import com.example.planificatorbuget.components.CustomDropdownMenuForTypeSelection
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.components.TransactionsBarChart
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.model.TransactionModelParcelable
import com.example.planificatorbuget.utils.Period

@Preview
@Composable
fun DailySummaryDetailedChartScreen(
    navController: NavController = NavController(LocalContext.current),
    transactions: List<TransactionModelParcelable> = emptyList()
) {

    val transactionList: List<TransactionModel> =
        TransactionModelParcelable.toTransactionModel(transactions)

    val typeOptions = listOf("Cheltuieli", "Venituri")
    var selectedTypeOption by remember { mutableStateOf(typeOptions[0]) }

    var selectedPeriod by remember { mutableStateOf(Period.LAST_7_DAYS) }


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
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                    Surface(modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth(), color = Color.White) {
                        TransactionsBarChart(
                            modifier = Modifier
                                .fillMaxWidth(),
                            transactions = transactionList,
                            period = selectedPeriod,
                            transactionType = if (selectedTypeOption == "Cheltuieli") "Cheltuiala" else "Venit",
                            showNameDay = false,
                            xAxisRotationAngle = 20f,
                            xAxisBottomPadding = 40.dp,
                            xAxisLabelFontSize = 9.sp
                        )
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        CustomDropdownMenuForPeriodSelection(
                            label = "Perioada:",
                            selectedOption = selectedPeriod
                        ) {
                            selectedPeriod = it
                        }
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        CustomDropdownMenuForTypeSelection(
                            label = "Tipul tranzactiei:",
                            options = typeOptions,
                            selectedOption = selectedTypeOption
                        ) {
                            selectedTypeOption = it
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}