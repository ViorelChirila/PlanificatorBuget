package com.example.planificatorbuget.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.FABContent
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.model.TransactionsModel
import com.example.planificatorbuget.utils.gradientBackgroundBrush

@Preview
@Composable
fun PlannerTransactionsScreen(navController: NavController = NavController(LocalContext.current)) {

    val originalListOfTransactions = remember {
        mutableStateOf(
            listOf(
                TransactionsModel(
                    "1",
                    "1",
                    20.0,
                    "Venit",
                    "Salariu",
                    "08/17/2023",
                    "Mancare petru caine"
                ),
                TransactionsModel(
                    "2",
                    "1",
                    20.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "08/17/2023",
                    "Service masina"
                ),
                TransactionsModel("3", "1", 20.0, "Venit", "Salariu", "08/17/2023", "1000"),
                TransactionsModel(
                    "4",
                    "1",
                    30.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "08/17/2023",
                    "Factura telefon"
                ),
                TransactionsModel("5", "1", 20.0, "Venit", "Salariu", "08/17/2023", "1000"),
                TransactionsModel(
                    "6",
                    "1",
                    20.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "08/17/2023",
                    "Altceva"
                ),
                TransactionsModel("7", "1", 20.0, "Venit", "Salariu", "08/17/2023", "1000"),
                TransactionsModel(
                    "8",
                    "1",
                    60.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "08/17/2023",
                    "Alta factura"
                ),
                TransactionsModel("9", "1", 10.0, "Venit", "Salariu", "08/17/2023", "1000"),
                TransactionsModel(
                    "10",
                    "1",
                    6.0,
                    "Cheltuiala",
                    "Cumparaturi",
                    "08/17/2023",
                    "Ceva"
                ),
            )
        )
    }
    val filteredListOfTransactions = remember { mutableStateOf(originalListOfTransactions.value) }


    Box(
        modifier = Modifier.background(
            brush = gradientBackgroundBrush(
                isVerticalGradient = true,
                colors = listOf(
                    Color(0xFF7F9191),
                    Color(0xffc3c3d8),
                    Color(0xff00d4ff)
                )
            )
        )
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Tranzactii",
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
            floatingActionButton = {
                FABContent {

                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = Color.Transparent
        ) { paddingValues ->
            Card(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(bottom = 10.dp, start = 7.dp, end = 7.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    SearchTransactionsByDateForm()
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    FilterAndSortTransactions(onSort = {
                        filteredListOfTransactions.value = filteredListOfTransactions.value.sortedBy { it.amount }
                    }) { query ->

                        if (query.isEmpty()) {
                            filteredListOfTransactions.value = originalListOfTransactions.value
                        } else {
                            filteredListOfTransactions.value = originalListOfTransactions.value.filter {
                                it.transactionDescription.contains(query, ignoreCase = true)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TransactionsList(lisOfTransactions = filteredListOfTransactions.value)
                }
            }

        }

    }

}


@Composable
private fun FilterAndSortTransactions(
    onSort: () -> Unit = { },
    onSearch: (String) -> Unit = { },
) {

    val searchQueryState = rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val valid = remember(searchQueryState.value) {
        searchQueryState.value.trim().isNotEmpty()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { onSort() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(5.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                Text(text = "Sorteaza")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { /* TODO: Filter action */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(5.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
                Text(text = "Filtreaza")
            }
        }
        Spacer(modifier = Modifier.width(30.dp))
        OutlinedTextField(
            value = searchQueryState.value,
            onValueChange = { searchQueryState.value = it },
            label = { Text("Cauta tranzactii", style = MaterialTheme.typography.labelLarge) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(),
            shape = RoundedCornerShape(30),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions {
                onSearch(searchQueryState.value.trim())
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        )

    }
}

@Composable
private fun SearchTransactionsByDateForm() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
            Text(
                "Tranzactii",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(text = "08/17/2023")
        }
        Spacer(modifier = Modifier.width(50.dp))
        OutlinedTextField(
            value = "08/17/2023",
            onValueChange = {},
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun TransactionsList(lisOfTransactions: List<TransactionsModel> = emptyList()) {

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(5.dp)) {
        items(items = lisOfTransactions) { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}

//@Preview
@Composable
fun TransactionItem(transaction: TransactionsModel) {
    Card(modifier = Modifier
        .clickable { }
        .fillMaxWidth()
        .padding(3.dp),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = transaction.transactionDescription, fontWeight = FontWeight.Bold)
                Text(
                    text = "Categorie: ${transaction.categoryId}",
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
                Text(text = transaction.transactionDate, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Text(
                    text = transaction.amount.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (transaction.transactionType == "Venit") Color.Green else Color.Red
                )
            }

        }

    }
}
