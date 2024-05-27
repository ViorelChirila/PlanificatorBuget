package com.example.planificatorbuget.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.planificatorbuget.screens.account.PlannerAccountScreen
import com.example.planificatorbuget.screens.accountsettings.PlannerAccountSettingsScreen
import com.example.planificatorbuget.screens.home.PlannerHomeScreen
import com.example.planificatorbuget.screens.login.PlannerLoginScreen
import com.example.planificatorbuget.screens.notification.PlannerNotificationsScreen
import com.example.planificatorbuget.screens.register.PlannerCreateAccountScreen
import com.example.planificatorbuget.screens.statistics.PlannerStatisticsScreen
import com.example.planificatorbuget.screens.transactions.PlannerTransactionsScreen

@Composable
fun PlannerNavigation(startDestination: String) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination ){

        navigation(
            startDestination = PlannerScreens.LoginScreen.name,
            route = "auth"
        ){
            composable(PlannerScreens.LoginScreen.name){
                PlannerLoginScreen(navController = navController)
            }

            composable(PlannerScreens.CreateAccountScreen.name){
                PlannerCreateAccountScreen(navController = navController)
            }
        }

        composable(PlannerScreens.HomeScreen.name){
            PlannerHomeScreen(navController = navController)
        }

        composable(PlannerScreens.AccountScreen.name){
            PlannerAccountScreen(navController = navController)
        }

        composable(PlannerScreens.NotificationsScreen.name){
             PlannerNotificationsScreen(navController = navController)
        }

        composable(PlannerScreens.AccountSettingsScreen.name){
            PlannerAccountSettingsScreen(navController = navController)
        }

        composable(PlannerScreens.StatisticsScreen.name){
            PlannerStatisticsScreen(navController = navController)
        }

        composable(PlannerScreens.TransactionsScreen.name){
            PlannerTransactionsScreen(navController = navController)
        }

    }
}