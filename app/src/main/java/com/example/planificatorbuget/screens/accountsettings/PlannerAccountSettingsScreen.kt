package com.example.planificatorbuget.screens.accountsettings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.planificatorbuget.R
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.utils.gradientBackgroundBrush

@Preview
@Composable
fun PlannerAccountSettingsScreen(navController: NavController = NavController(LocalContext.current)) {
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
                    title = "Setari cont",
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
            Box(modifier = Modifier.padding(paddingValues)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .padding(bottom = 15.dp, start = 15.dp, end = 15.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(150.dp)

                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    shape = CircleShape,
                                    shadowElevation = 5.dp,
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.profil_avatar),
                                        contentDescription = "Profile picture",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clickable { }
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit profile picture",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(Color.Black, shape = CircleShape)
                                        .padding(4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Nume utilizator",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

        }

    }
}