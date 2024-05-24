package com.example.planificatorbuget.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.utils.gradientBackgroundBrush

@Preview
@Composable
fun PlannerAccountScreen(navController: NavController = NavController(LocalContext.current)) {
    Box(modifier = Modifier.background(
        brush = gradientBackgroundBrush(
            isVerticalGradient = true,
            colors = listOf(
                Color(0xff3f4848),
                Color(0xffc3c3d8),
                Color(0xff00d4ff)
            )
        )
    )) {
        Scaffold(
            bottomBar = {
                NavigationBarComponent(navController = navController)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Text(text = "Account Screen")

            }
        }
    }
}

