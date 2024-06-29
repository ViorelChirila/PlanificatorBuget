package com.example.planificatorbuget.screens.chartdetailsscreeens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.planificatorbuget.R
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.CustomDropdownMenuForPeriodSelection
import com.example.planificatorbuget.components.CustomDropdownMenuForSamplingPeriodSelection
import com.example.planificatorbuget.components.GroupedBarChart
import com.example.planificatorbuget.components.LegendComponent
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.model.TransactionModelParcelable
import com.example.planificatorbuget.utils.SamplingPeriod
import com.example.planificatorbuget.utils.prepareDataForGroupedBarChart

@Composable
fun FinancialFluxDetailedScreen(
    navController: NavController = NavController(LocalContext.current),
    transactions: List<TransactionModelParcelable> = emptyList()
) {
    val transactionList: List<TransactionModel> =
        TransactionModelParcelable.toTransactionModel(transactions)

    var selectedPeriod by remember { mutableStateOf(SamplingPeriod.MONTHLY) }
    var periodCount by remember { mutableStateOf("3") }
    val chartData = prepareDataForGroupedBarChart(transactionList, selectedPeriod, periodCount.toIntOrNull() ?: 3)
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
                            .height(400.dp)
                            .fillMaxWidth(), color = Color.White
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            GroupedBarChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp),
                                chartData = chartData,
                                xAxisRotationAngle = 10f
                            )
                            LegendComponent()
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        CustomDropdownMenuForSamplingPeriodSelection(
                            label = "Periodicitate:",
                            selectedOption = selectedPeriod
                        ) {
                            selectedPeriod = it
                        }
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)) {
                        OutlinedTextField(
                            value = periodCount,
                            onValueChange = { periodCount = it },
                            label = { Text("Numarul de luni/zile/săptămâni") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            modifier = Modifier,

                        )
                    }
                }
            }
        }
    }
}