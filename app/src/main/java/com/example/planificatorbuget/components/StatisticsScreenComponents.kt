package com.example.planificatorbuget.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.utils.Period
import com.example.planificatorbuget.utils.SamplingPeriod
import com.example.planificatorbuget.utils.getRecentTwoMonthsData
import com.example.planificatorbuget.utils.prepareDataForGroupedBarChart

@Preview
@Composable
fun SummaryChartCard(
    listOfTransactions: List<TransactionModel> = emptyList(),
    selectedOption: String = "",
    meanValue: Double = 0.0,
    options: List<String> = emptyList(),
    onDetailsClicked: () -> Unit = {}
) {
    var selectedOptionLocal by remember {
        mutableStateOf(selectedOption)
    }
    var meanValueLocal by remember {
        mutableDoubleStateOf(meanValue)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            Text(
                text = "Sumar zilnic",
                modifier = Modifier.padding(
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 5.dp
                ),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), modifier = Modifier
                    .padding(top = 15.dp, bottom = 10.dp)
                    .height(290.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TransactionsBarChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        listOfTransactions,
                        Period.LAST_7_DAYS,
                        transactionType = if (selectedOptionLocal == "Cheltuieli") "Cheltuiala" else "Venit"
                    ) {
                        meanValueLocal = it
                    }
                    LegendComponent()
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = if (selectedOptionLocal == "Cheltuieli") "Media cheltuielilor pe 7 zile: " else "Media veniturilor pe 7 zile: ",
                        fontSize = 15.sp
                    )
                    CustomDropdownMenuForTypeSelection(
                        label = "Tipul tranzacției",
                        options = options,
                        selectedOption = selectedOptionLocal,
                        onOptionSelected = { selectedOptionLocal = it }
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "%.2f".format(meanValueLocal) + " Lei", fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(35.dp))
                    TextButton(
                        onClick = { onDetailsClicked() },
                        modifier = Modifier.padding(end = 5.dp, bottom = 2.dp)
                    ) {
                        Text(text = "Vezi detalii")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FinancialFlux(
    listOfTransactions: List<TransactionModel> = emptyList(),
    onDetailsClicked: () -> Unit = {}
) {
    val chartData = prepareDataForGroupedBarChart(listOfTransactions, SamplingPeriod.MONTHLY, 3)
    Log.d("ChartData", chartData.toString())
    val recentData = getRecentTwoMonthsData(chartData)
    Log.d("RecentData", recentData.toString())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            Text(
                text = "Flux Financiar",
                modifier = Modifier.padding(
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 5.dp
                ),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), modifier = Modifier
                    .padding(top = 15.dp, bottom = 10.dp)
                    .height(290.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GroupedBarChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        chartData = chartData
                    )
                    LegendComponent()
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(text = "Tipul tranzactiei", fontWeight = FontWeight.Bold)
                    Text(text = "Intrari")
                    Text(text = "Iesiri")
                    Text(text = "Flux financiar", fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(text = recentData[0].first, fontWeight = FontWeight.Bold)
                    Text(text = " "+recentData[0].second.second.toString())
                    Text(text = "${-recentData[0].second.first}")
                    Text(text = (recentData[0].second.second - recentData[0].second.first).toString(), fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(text = recentData[1].first, fontWeight = FontWeight.Bold)
                    Text(text = " "+recentData[1].second.second.toString())
                    Text(text = "${-recentData[1].second.first}")
                    Text(text = (recentData[1].second.second - recentData[1].second.first).toString(), fontWeight = FontWeight.Bold)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onDetailsClicked() },
                    modifier = Modifier.padding(end = 5.dp, bottom = 2.dp)
                ) {
                    Text(text = "Vezi detalii")
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenuForTypeSelection(
    label: String,
    options: List<String>,
    selectedOption: String,
    color: Color = Color.Transparent,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(selectedOption) }

    Column(
        modifier = Modifier
            .padding(top = 5.dp)
    ) {
        Text(text = label)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .width(150.dp)
                    .menuAnchor(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = color,
                    unfocusedContainerColor = color,
                    disabledContainerColor = color
                )

            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        textFieldValue = option
                        onOptionSelected(option)
                        expanded = false
                    },
                        text = { Text(text = option) })
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenuForPeriodSelection(
    label: String,
    selectedOption: Period,
    onOptionSelected: (Period) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(selectedOption) }
    val options = Period.entries

    val periodDisplayName = { period: Period ->
        when (period) {
            Period.LAST_7_DAYS -> "ultimele 7 zile"
            Period.LAST_MONTH -> "ultimele 30 de zile"
            Period.LAST_3_MONTHS -> "ultimele 3 luni"
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 5.dp)
    ) {
        Text(text = label, fontSize = 15.sp)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = periodDisplayName(textFieldValue),
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .width(220.dp)
                    .menuAnchor(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )

            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        textFieldValue = option
                        onOptionSelected(option)
                        expanded = false
                    },
                        text = { Text(text = periodDisplayName(option)) })
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenuForSamplingPeriodSelection(
    label: String,
    selectedOption: SamplingPeriod,
    onOptionSelected: (SamplingPeriod) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(selectedOption) }
    val options = SamplingPeriod.entries

    val periodDisplayName = { period: SamplingPeriod ->
        when (period) {
            SamplingPeriod.DAILY -> "Zilinic"
            SamplingPeriod.MONTHLY -> "Lunar"
            SamplingPeriod.WEEKLY -> "Saptamanal"
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 5.dp)
    ) {
        Text(text = label, fontSize = 15.sp)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = periodDisplayName(textFieldValue),
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .width(220.dp)
                    .menuAnchor(),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )

            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        textFieldValue = option
                        onOptionSelected(option)
                        expanded = false
                    },
                        text = { Text(text = periodDisplayName(option)) })
                }

            }

        }
    }
}

