package com.example.planificatorbuget.screens.transactiondetailsscreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.NavigationBarComponent

@Preview
@Composable
fun PlannerTransactionDetailsScreen(
    navController: NavController = NavController(LocalContext.current),
    transactionId: String? = "",
    detailsScreenViewModel: TransactionDetailsScreenViewModel = hiltViewModel()
) {
    detailsScreenViewModel.fetchTransactionById(transactionId!!)

    val transactionState by detailsScreenViewModel.transaction.collectAsState()
    Surface {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Detalii tranzactie",
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
                if (transactionState.isLoading==true)
                {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = transactionState.data!!.transactionTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                        Text(
                            text = "Valoare: ${transactionState.data!!.amount}",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(10.dp)
                        )
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
                            Text(text = "06/15/2024", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.width(20.dp))
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "time icon"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "16:30", style = MaterialTheme.typography.bodyLarge)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Surface(shape = CircleShape) {
                                AsyncImage(
                                    model = "",
                                    contentDescription = "icon",
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(70.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Nume categorie",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider()
                        Column {
                            var text by remember {
                                mutableStateOf(
                                    "This is a large block of text that can be edited by the user. " +
                                            "You can add more text here as needed. The user should be able to scroll through this text and " +
                                            "edit any part of it. This block of text is initially set with a default value, but it can be " +
                                            "changed by the user to anything they like."
                                )
                            }

                            OutlinedTextField(
                                value = text,
                                onValueChange = { newText -> text = newText },
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                TextButton(onClick = { /*TODO*/ }) {
                                    Text(text = "Editeaza")
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}