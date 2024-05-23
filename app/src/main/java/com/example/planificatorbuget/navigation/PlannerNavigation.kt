package com.example.planificatorbuget.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planificatorbuget.screens.account.PlannerAccountScreen
import com.example.planificatorbuget.screens.register.PlannerCreateAccountScreen
import com.example.planificatorbuget.screens.home.PlannerHomeScreen
import com.example.planificatorbuget.screens.login.PlannerLoginScreen

@Composable
fun PlannerNavigation(startDestination: String) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination ){

        composable(PlannerScreens.HomeScreen.name){
            PlannerHomeScreen(navController = navController)
        }

        composable(PlannerScreens.LoginScreen.name){
            PlannerLoginScreen(navController = navController)
        }

        composable(PlannerScreens.CreateAccountScreen.name){
            PlannerCreateAccountScreen(navController = navController)
        }

        composable(PlannerScreens.AccountScreen.name){
            PlannerAccountScreen(navController = navController)
        }

    }
}