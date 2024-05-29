package com.example.planificatorbuget.screens.transactions

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.utils.gradientBackgroundBrush

@Preview
@Composable
fun PlannerTransactionsScreen(navController: NavController = NavController(LocalContext.current)) {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tranzactii", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(50.dp))
                        OutlinedTextField(
                            value = "08/17/2023",
                            onValueChange = {},
                            label = { Text("Date") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
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
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Filter")
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
                            value = "Hinted search text",
                            onValueChange = {},
                            label = { Text("Cauta tranzactii") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(),
                            shape = RoundedCornerShape(30),
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Sort")
                            }

                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

        }

    }

}