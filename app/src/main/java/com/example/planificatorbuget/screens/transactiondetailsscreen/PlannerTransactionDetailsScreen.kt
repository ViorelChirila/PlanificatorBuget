package com.example.planificatorbuget.screens.transactiondetailsscreen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.utils.formatTimestampToString

@Preview
@Composable
fun PlannerTransactionDetailsScreen(
    navController: NavController = NavController(LocalContext.current),
    transactionId: String? = "",
    detailsScreenViewModel: TransactionDetailsScreenViewModel = hiltViewModel()
) {
    val transactionState by detailsScreenViewModel.transaction.collectAsState()
    val categoryState by detailsScreenViewModel.categories.collectAsState()
    val resultForUpdate by detailsScreenViewModel.transactionUpdateResult.observeAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        detailsScreenViewModel.fetchTransactionAndCategoryById(transactionId!!)
    }

    var enableEdit by remember { mutableStateOf(false) }
    Surface {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Detalii tranzacție",
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
                if (transactionState.isLoading == true || categoryState.isLoading == true) {
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
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = transactionState.data!!.transactionTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = "Valoare: ",
                                style = MaterialTheme.typography.headlineSmall,

                                )
                            val icon = if (transactionState.data!!.transactionType == "Venit") {
                                Icons.Default.Add
                            } else {
                                Icons.Default.Remove
                            }
                            val color = if (transactionState.data!!.transactionType == "Venit") {
                                Color(0xFF349938)
                            } else {
                                Color.Red
                            }
                            Icon(imageVector = icon, contentDescription = "signIcon", tint = color)
                            Text(
                                text = if (transactionState.data!!.transactionType == "Venit") transactionState.data!!.amount.toString() else "${transactionState.data!!.amount*-1}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = color,
                                )
                        }
                        HorizontalDivider()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "date icon"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = formatTimestampToString(transactionState.data!!.transactionDate),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "time icon"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = formatTimestampToString(
                                    pattern = "HH:mm",
                                    timestamp = transactionState.data!!.transactionDate
                                ), style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Surface(shape = CircleShape, border = BorderStroke(1.dp, Color.Black)) {
                                AsyncImage(
                                    model = categoryState.data!!.categoryIcon.toUri(),
                                    contentDescription = "icon",
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(70.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = categoryState.data!!.categoryName,
                                style = MaterialTheme.typography.bodyLarge, fontSize = 20.sp
                            )
                        }
                        HorizontalDivider()
                        Column {
                            var text by remember {
                                mutableStateOf(
                                    transactionState.data!!.transactionDescription
                                )
                            }

                            OutlinedTextField(
                                value = text,
                                onValueChange = { newText -> text = newText },
                                readOnly = !enableEdit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .padding(top = 15.dp, start = 15.dp, end = 15.dp),
                                label = { Text("Descriere") }
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp)
                            ) {
                                TextButton(onClick = {showDialog = true}) {
                                    Text(text = "Arată poza descriere")
                                }
                                TextButton(onClick = {
                                    if(enableEdit && text != transactionState.data!!.transactionDescription) {
                                        detailsScreenViewModel.updateTransactionDescription(transactionState.data!!.transactionId!!, text)
                                    }
                                    enableEdit = !enableEdit
                                }) {
                                    Text(text = if (enableEdit) "Salvează" else "Editează")
                                }
                            }
                        }

                    }
                    LaunchedEffect(resultForUpdate) {
                        when (resultForUpdate) {
                            is Response.Loading -> {
                                Toast.makeText(context, "Se actualizează...", Toast.LENGTH_SHORT).show()
                            }

                            is Response.Success -> {
                                if ((resultForUpdate as Response.Success).data == true) {
                                    Toast.makeText(context, "Tranzacție actualizată cu succes", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }

                            is Response.Error -> {
                                Toast.makeText(
                                    context,
                                    (resultForUpdate as Response.Error).message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> { /* no-op */
                            }
                        }
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            confirmButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Close")
                                }
                            },
                            title = {
                                Text(text = "Imagine descriere tranzacție")
                            },
                            text = {
                                AsyncImage(
                                    model = transactionState.data!!.descriptionImage, // Replace with your image resource
                                    contentDescription = "Sample Image",
                                    modifier = Modifier.size(300.dp) // Adjust size as needed
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}