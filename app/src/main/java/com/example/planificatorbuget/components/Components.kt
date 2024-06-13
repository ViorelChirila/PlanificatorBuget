package com.example.planificatorbuget.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun FABContent(onTap: () -> Unit) {
    FloatingActionButton(
        onClick = { onTap() },
        shape = RoundedCornerShape(50.dp),
        containerColor = Color(0xFF92CBDF),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add a new transaction",
            tint = Color.White
        )
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    enable: Boolean = true,
    onDateSelected: (String) -> Unit,
    onClearDate: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val formattedDate =
                String.format("%02d/%02d/%d", selectedMonth + 1, selectedDayOfMonth, selectedYear)
            onDateSelected(formattedDate)
        }, year, month, day
    )


    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (enable)datePickerDialog.show() },
        enabled = false,
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Black,
            disabledLabelColor = Color.Black,
            disabledIndicatorColor = Color.Black,
            disabledContainerColor = Color.White
        ),
        trailingIcon = {
            if (selectedDate.isNotEmpty()) {
                IconButton(onClick = { if(enable)onClearDate() }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear Date")
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DropDownMenuForRecurringPeriod(
    enable: Boolean,
    transactionRecurrenceInterval: String,
    onIntervalChanged: (String) -> Unit = { _ -> }
) {
    var expandedInterval by remember { mutableStateOf(false) }
    var recurrenceInterval by remember { mutableStateOf(transactionRecurrenceInterval) }
    ExposedDropdownMenuBox(
        expanded = expandedInterval && enable,
        onExpandedChange = { expandedInterval = !expandedInterval }) {
        OutlinedTextField(
            value = recurrenceInterval,
            onValueChange = { /* No-op */ },
            label = { Text("Interval recurență") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow"
                )
            },
            readOnly = true,
            enabled = enable
        )
        ExposedDropdownMenu(
            expanded = expandedInterval && enable,
            onDismissRequest = { expandedInterval = false }
        ) {
            DropdownMenuItem(onClick = {
                recurrenceInterval = "Zilnic"
                expandedInterval = false
                onIntervalChanged("Zilnic")
            }, text = { Text("Zilnic") })

            DropdownMenuItem(onClick = {
                recurrenceInterval = "Săptămânal"
                expandedInterval = false
                onIntervalChanged("Saptamanal")
            }, text = { Text("Săptămânal") })

            DropdownMenuItem(onClick = {
                recurrenceInterval = "Lunar"
                expandedInterval = false
                onIntervalChanged("Lunar")
            }, text = { Text("Lunar") })
        }
    }
}
