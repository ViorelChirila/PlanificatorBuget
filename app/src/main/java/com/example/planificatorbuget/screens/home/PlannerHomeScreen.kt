package com.example.planificatorbuget.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.navigation.PlannerScreens
import com.example.planificatorbuget.screens.SharedViewModel
import com.example.planificatorbuget.utils.convertMillisToDate
import com.example.planificatorbuget.utils.gradientBackgroundBrush
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PlannerHomeScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: SharedViewModel = hiltViewModel()
) {

    val dataOrException by viewModel.data.observeAsState()
    val user = dataOrException?.data
    val isLoading = dataOrException?.isLoading ?: true

    Box(
        modifier = Modifier.background(
            brush = gradientBackgroundBrush(
                isVerticalGradient = true,
                colors = if (!isSystemInDarkTheme()) listOf(
                    Color(0xFF7F9191),
                    Color(0xffc3c3d8),
                    Color(0xff00d4ff)
                ) else listOf(
                    Color(0xFF332D2D),
                    Color(0xFF232D52),
                    Color(0xFF1442A0)
                )
            )
        )
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Acasa",
                    haveNotifications = false,
                    isHomeScreen = true,
                    navController = navController
                )
            },
            bottomBar = {
                NavigationBarComponent(navController = navController)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Card(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth()
                                .height(200.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF258B41)
                            ),
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${user?.currentBudget} Lei",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                        }
                        Card(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val state =
                                    rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
                                DatePicker(
                                    state = state, modifier = Modifier.padding(8.dp),
                                    title = null,
                                    headline = {
                                        Text(
                                            text = "Alege o data",
                                            modifier = Modifier.padding(start = 10.dp)
                                        )
                                    },
                                )

                                val formattedDate = state.selectedDateMillis?.let { millis ->
                                    convertMillisToDate(millis)
                                } ?: "no selection"
                                Text(
                                    text = "Entered date timestamp: $formattedDate",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.End) {
                                    Button(onClick = {
                                        val encodedDate = URLEncoder.encode(formattedDate, StandardCharsets.UTF_8.toString())
                                        navController.navigate("${PlannerScreens.TransactionsScreen.name}/${encodedDate}")
                                    }) {
                                        Text(text="Detalii")
                                    }
                                }
                            }
                        }

                    }

                }

            }
        }
    }
}