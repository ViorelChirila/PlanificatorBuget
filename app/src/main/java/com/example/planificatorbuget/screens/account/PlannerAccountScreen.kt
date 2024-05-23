package com.example.planificatorbuget.screens.account

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.planificatorbuget.components.NavigationBarComponent

@Composable
fun PlannerAccountScreen(navController: NavController) {
    Scaffold(bottomBar = {
        NavigationBarComponent(navController = navController)
    }) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Text(text = "Account Screen")

        }
    }
}