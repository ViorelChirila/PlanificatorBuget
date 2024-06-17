package com.example.planificatorbuget.screens.csvuploadscreen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.planificatorbuget.R
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.navigation.PlannerScreens
import com.example.planificatorbuget.screens.SharedViewModel
import com.example.planificatorbuget.utils.formatTimestampToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvUploadScreen(
    viewModelUpload: CsvViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    viewModel: CsvUploadFileScreenViewModel = hiltViewModel(),
    navController: NavController = NavController(LocalContext.current)
) {
    val categoriesData by viewModel.categories.collectAsState()
    val csvContent by viewModelUpload.transactions.observeAsState()
    val listOfCategories by remember {
        derivedStateOf {
            categoriesData.data ?: emptyList()
        }
    }
    val errorMessage by viewModelUpload.errorMessage.observeAsState()
    val isProcessing by viewModel.isProcessing.observeAsState(false)
    val transactionAdditionStatus by viewModel.transactionAdditionStatus.observeAsState()

    var extendedCategory by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d("uri", uri.toString())
            viewModelUpload.readCsvFile(it)
        }
    }

    Surface {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Importa tranzactii din CSV",
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
                if (categoriesData.isLoading == true) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Incarca un fisier CSV pentru a importa tranzactii. Fisierul trebuie sa respecte formatul din poza de mai jos")
                        Image(
                            painter = painterResource(id = R.drawable.exemplu),
                            contentDescription = "exemplu csv",
                            modifier = Modifier.fillMaxWidth().size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Alege o categorie in care se incadreaza aceste tranzactii")
                        ExposedDropdownMenuBox(
                            expanded = extendedCategory,
                            onExpandedChange = { extendedCategory = !extendedCategory }) {
                            OutlinedTextField(
                                value = categoryName,
                                onValueChange = { /* No-op */ },
                                label = { Text(text = "Categorie") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown Arrow"
                                    )
                                },
                                readOnly = true
                            )
                            ExposedDropdownMenu(
                                expanded = extendedCategory,
                                onDismissRequest = { extendedCategory = false },
                                modifier = Modifier.heightIn(max = 200.dp)
                            ) {
                                listOfCategories.forEach { item ->
                                    DropdownMenuItem(onClick = {
                                        categoryId = item.categoryId
                                        categoryName = item.categoryName
                                        extendedCategory = false
                                    }, text = { Text(text = item.categoryName) })
                                }
                                DropdownMenuItem(
                                    text = { Text(text = "Adauga categorie noua") },
                                    onClick = {
                                        navController.navigate(PlannerScreens.CategoriesScreen.name)
                                    })

                            }

                        }
                        Row {
                            Button(onClick = { launcher.launch("*/*") }, enabled = !isProcessing) {
                                Text("Incarca fisier")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = {
                                    csvContent!!.forEach { transactionModel ->
                                        transactionModel.categoryId = categoryId
                                    }
                                    viewModel.addNewTransactions(csvContent ?: emptyList())
                                },
                                enabled = csvContent != null && !isProcessing && categoryId.isNotEmpty()
                            ) {
                                Text(text = "Adauga tranzactii")
                            }
                        }
                        transactionAdditionStatus?.let {
                            val message =
                                if (it) "All transactions added successfully" else "Error adding some transactions"
                            Text(text = message)
                            viewModel.resetTransactionAdditionStatus()
                            viewModelUpload.resetTransactions()

                        }

                        csvContent?.let {
                            Text(text = "Fiserul a fost incarcat cu succes")
                            LazyColumn {
                                items(it) { transaction ->
                                    TransactionItemFromCSV(transaction = transaction)
                                }
                            }
                        }

                        errorMessage?.let {
                            Text("Error: $it")
                        }

                    }
                }

            }
        }
    }
}

@Composable
fun TransactionItemFromCSV(transaction: TransactionModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Titlu: ${transaction.transactionTitle}",
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(text = "Valoare: ${transaction.amount}", fontSize = 16.sp, color = Color.DarkGray)
            Text(
                text = "Tip: ${transaction.transactionType}",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = "Data: ${
                    formatTimestampToString(
                        transaction.transactionDate,
                        "MM/dd/yyyy HH:mm"
                    )
                }", fontSize = 16.sp, color = Color.DarkGray
            )
            Text(
                text = "Descriere: ${transaction.transactionDescription}",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}