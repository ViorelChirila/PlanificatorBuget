package com.example.planificatorbuget.screens.chartdetailsscreeens

import android.util.Log
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.BudgetEvolutionLineChart
import com.example.planificatorbuget.components.CustomDropdownMenuForPredictionAlgorithmSelection
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.model.TransactionModelParcelable

import com.example.planificatorbuget.utils.PredictionModel
import com.example.planificatorbuget.utils.calculateCumulativeBudget
import com.example.planificatorbuget.utils.predictFutureBudgetAR
import com.example.planificatorbuget.utils.predictFutureBudgetARMA

@Composable
fun BudgetEvolutionDetailedChartScreen(
    navController: NavController = NavController(LocalContext.current),
    transactions: List<TransactionModelParcelable> = emptyList(),
    initialBudget: Double = 0.0,
    ) {
    val transactionList: List<TransactionModel> =
        TransactionModelParcelable.toTransactionModel(transactions)

    var maxArOrder = 2
    var maxArmaOrderP = 2
    var maxArmaOrderQ = 1
    var showPrediction by remember { mutableStateOf(false) }
    var monthsAhead by remember { mutableStateOf("1") }
    var selectedModel by remember { mutableStateOf(PredictionModel.AR) }

    val historicalData = calculateCumulativeBudget(transactionList, initialBudget = initialBudget)

    Log.d("hisDatas","historicalData: ${historicalData.size}")
    when(historicalData.size){
        in 0..10 -> {
            maxArOrder = 1
            maxArmaOrderP = 1
            maxArmaOrderQ = 1
        }
        in 10..20 -> {
            maxArOrder = 2
            maxArmaOrderP = 2
            maxArmaOrderQ = 1
        }
        in 21..50 -> {
            maxArOrder = 3
            maxArmaOrderP = 3
            maxArmaOrderQ = 2
        }
        in 51..100 -> {
            maxArOrder = 4
            maxArmaOrderP = 4
            maxArmaOrderQ = 3
        }
    }

    val predictedData = when (selectedModel) {
        PredictionModel.AR -> predictFutureBudgetAR(
            transactionList,
            order = maxArOrder,
            intervalsAhead = monthsAhead.toIntOrNull() ?: 1,
            initialBudget
        )

        PredictionModel.ARMA -> predictFutureBudgetARMA(
            transactionList,
            maxArmaOrderP,
            maxArmaOrderQ,
            intervalsAhead = monthsAhead.toIntOrNull() ?: 1,
            initialBudget
        )
    }
    Log.d("Chart", "BudgetEvolutionDetailedChartScreen: $historicalData")

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager: FocusManager = LocalFocusManager.current

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
                            .height(if (showPrediction) 810.dp else 400.dp)
                            .fillMaxWidth(), color = Color.White
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            BudgetEvolutionLineChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp),
                                chartData = historicalData,
                                xAxisRotationAngle = 0f,
                                xAxisLabelFontSize = 10.sp,
                                lineStyleColor = Color(0x9E4330AA),
                                shadowUnderLine = Color(0x9E4330AA),
                                intersectionPointRadius = 4.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider()
                            if (showPrediction) {
                                BudgetEvolutionLineChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp),
                                    chartData = predictedData,
                                    xAxisRotationAngle = 0f,
                                    xAxisLabelFontSize = 10.sp,
                                    lineStyleColor = Color(0x9E45DB35),
                                    shadowUnderLine = Color(0x9E45DB35),
                                    intersectionPointRadius = 4.dp
                                )
                            }
                        }
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Afiseaza predictie", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        Switch(checked = showPrediction, onCheckedChange = { showPrediction = it })
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomDropdownMenuForPredictionAlgorithmSelection(
                            label = "Model predictie:",
                            selectedOption = selectedModel,
                            onOptionSelected = { selectedModel = it }
                        )
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        OutlinedTextField(
                            value = monthsAhead,
                            onValueChange = { monthsAhead = it },
                            label = { Text("Numarul de luni pentru predictie:") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            modifier = Modifier,
                            enabled = showPrediction
                            )
                    }
                }
            }
        }
    }
}