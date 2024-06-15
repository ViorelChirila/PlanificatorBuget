package com.example.planificatorbuget.screens.chartdetailsscreeens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.planificatorbuget.components.ChartType
import com.example.planificatorbuget.components.CustomDropdownMenuForPeriodSelection
import com.example.planificatorbuget.components.CustomDropdownMenuForTypeSelection
import com.example.planificatorbuget.components.LegendComponent
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.components.TransactionsBarChart
import com.example.planificatorbuget.components.TransactionsLineChart
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

    var selectedChartType by remember { mutableStateOf(ChartType.BAR_CHART) }


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
                        if (selectedChartType == ChartType.BAR_CHART) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TransactionsBarChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(360.dp),
                                    transactions = transactionList,
                                    period = selectedPeriod,
                                    transactionType = if (selectedTypeOption == "Cheltuieli") "Cheltuiala" else "Venit",
                                    showNameDay = false,
                                    xAxisRotationAngle = 20f,
                                    xAxisBottomPadding = 20.dp,
                                    xAxisLabelFontSize = 9.sp
                                )
                                LegendComponent()
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TransactionsLineChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(360.dp),
                                    transactions = transactionList,
                                    period = selectedPeriod,
                                    transactionType = if (selectedTypeOption == "Cheltuieli") "Cheltuiala" else "Venit",
                                    showNameDay = false,
                                    xAxisRotationAngle = 20f,
                                    xAxisBottomPadding = 40.dp,
                                    xAxisLabelFontSize = 9.sp,
                                    lineStyleColor = if (selectedTypeOption == "Cheltuieli") Color.Red else Color(
                                        0xFF258B41
                                    ),
                                    shadowUnderLine = if (selectedTypeOption == "Cheltuieli") Color.Red else Color(
                                        0xFF258B41
                                    )
                                )
                                LegendComponent()
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        CustomDropdownMenuForPeriodSelection(
                            label = "Perioada:",
                            selectedOption = selectedPeriod
                        ) {
                            selectedPeriod = it
                        }
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        CustomDropdownMenuForTypeSelection(
                            label = "Tipul tranzactiei:",
                            options = typeOptions,
                            selectedOption = selectedTypeOption
                        ) {
                            selectedTypeOption = it
                        }
                    }
                    HorizontalDivider()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Alege tipul graficului",
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Surface(
                            modifier = Modifier
                                .width(100.dp),
                            border = BorderStroke(1.dp, Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { selectedChartType = ChartType.BAR_CHART }) {
                                    Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = "Bar chart icon"
                                    )
                                }
                                IconButton(onClick = { selectedChartType = ChartType.LINE_CHART }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ShowChart,
                                        contentDescription = "Line chart icon"
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