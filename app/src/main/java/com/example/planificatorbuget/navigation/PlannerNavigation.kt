package com.example.planificatorbuget.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.planificatorbuget.screens.account.AccountScreenViewModel
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
//    val viewModel = hiltViewModel<AccountScreenViewModel>()

    NavHost(navController = navController, startDestination = startDestination ){

        navigation(
            startDestination = PlannerScreens.LoginScreen.name,
            route = FunctionalitiesRoutes.Authentication.name
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

        composable(PlannerScreens.NotificationsScreen.name){
             PlannerNotificationsScreen(navController = navController)
        }

        navigation(
            startDestination = PlannerScreens.AccountScreen.name,
            route = FunctionalitiesRoutes.Account.name
        ){
            composable(PlannerScreens.AccountScreen.name){
                val viewModel = it.sharedViewModel<AccountScreenViewModel>(navController = navController)
                PlannerAccountScreen(navController = navController, viewModel = viewModel)
            }

            composable(PlannerScreens.AccountSettingsScreen.name){
                val viewModel = it.sharedViewModel<AccountScreenViewModel>(navController = navController)
                PlannerAccountSettingsScreen(navController = navController, viewModel = viewModel)
            }
        }


        composable(PlannerScreens.StatisticsScreen.name){
            PlannerStatisticsScreen(navController = navController)
        }

        composable(PlannerScreens.TransactionsScreen.name){
            PlannerTransactionsScreen(navController = navController)
        }

    }
}

@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentBackStackEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentBackStackEntry)
}