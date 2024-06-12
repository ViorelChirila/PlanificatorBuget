package com.example.planificatorbuget.components

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

@Preview
@Composable
fun SummaryChartCard(
    listOfTransactions: List<TransactionModel> = emptyList(),
    selectedOption: String = "",
    meanValue: Double = 0.0,
    options: List<String> = emptyList()
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
                    CustomDropdownMenu(
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
                        onClick = { },
                        modifier = Modifier.padding(end = 5.dp, bottom = 2.dp)
                    ) {
                        Text(text = "Vezi detalii")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String,
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
