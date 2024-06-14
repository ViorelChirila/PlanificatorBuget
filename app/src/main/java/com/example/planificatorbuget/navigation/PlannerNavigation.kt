package com.example.planificatorbuget.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.model.TransactionModelParcelable
import com.example.planificatorbuget.screens.SharedViewModel
import com.example.planificatorbuget.screens.account.PlannerAccountScreen
import com.example.planificatorbuget.screens.accountsettings.PlannerAccountSettingsScreen
import com.example.planificatorbuget.screens.categories.CategoriesScreenViewModel
import com.example.planificatorbuget.screens.categories.PlannerCategoriesScreen
import com.example.planificatorbuget.screens.chartdetailsscreeens.DailySummaryDetailedChartScreen
import com.example.planificatorbuget.screens.home.PlannerHomeScreen
import com.example.planificatorbuget.screens.login.PlannerLoginScreen
import com.example.planificatorbuget.screens.notification.PlannerNotificationsScreen
import com.example.planificatorbuget.screens.recurringtransactions.PlannerRecurringTransactionsScreen
import com.example.planificatorbuget.screens.register.PlannerCreateAccountScreen
import com.example.planificatorbuget.screens.statistics.PlannerStatisticsScreen
import com.example.planificatorbuget.screens.transactions.PlannerTransactionsScreen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun PlannerNavigation(startDestination: String) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        navigation(
            startDestination = PlannerScreens.LoginScreen.name,
            route = FunctionalitiesRoutes.Authentication.name
        ) {
            composable(PlannerScreens.LoginScreen.name) {
                PlannerLoginScreen(navController = navController)
            }

            composable(PlannerScreens.CreateAccountScreen.name) {
                PlannerCreateAccountScreen(navController = navController)
            }
        }

        navigation(
            startDestination = PlannerScreens.HomeScreen.name,
            route = FunctionalitiesRoutes.Main.name
        ) {
            composable(PlannerScreens.HomeScreen.name) {
                val viewModel = it.sharedViewModel<SharedViewModel>(navController)
                PlannerHomeScreen(navController = navController, viewModel = viewModel)
            }

            composable(PlannerScreens.NotificationsScreen.name) {
                PlannerNotificationsScreen(navController = navController)
            }


            composable(PlannerScreens.AccountScreen.name) {
                val viewModel = it.sharedViewModel<SharedViewModel>(navController)
                PlannerAccountScreen(navController = navController, viewModel = viewModel)
            }

            composable(PlannerScreens.AccountSettingsScreen.name) {
                val viewModel = it.sharedViewModel<SharedViewModel>(navController)
                PlannerAccountSettingsScreen(navController = navController, viewModel = viewModel)
            }

            composable(PlannerScreens.StatisticsScreen.name) {
                PlannerStatisticsScreen(navController = navController)
            }


            val transactionName = PlannerScreens.TransactionsScreen.name
            composable("$transactionName/{selectedDate}", arguments = listOf(navArgument("selectedDate") {
                type = NavType.StringType
            })){backStackEntry ->
                val selectedDate = backStackEntry.arguments?.getString("selectedDate")
                val categoriesSharedViewModel =
                    backStackEntry.sharedViewModel<CategoriesScreenViewModel>(navController)
                val viewModel = backStackEntry.sharedViewModel<SharedViewModel>(navController)
                PlannerTransactionsScreen(
                    navController = navController,
                    categoriesSharedViewModel = categoriesSharedViewModel,
                    sharedViewModel = viewModel,
                    selectedDate = selectedDate ?: ""
                )
            }
            composable(PlannerScreens.CategoriesScreen.name) {
                val viewModel = it.sharedViewModel<SharedViewModel>(navController)
                val categoriesSharedViewModel =
                    it.sharedViewModel<CategoriesScreenViewModel>(navController)
                PlannerCategoriesScreen(
                    navController = navController,
                    sharedViewModel = viewModel,
                    categoriesSharedViewModel = categoriesSharedViewModel
                )
            }

            val dailySummaryDetailedChartScreenName = PlannerScreens.DailySummaryDetailedChartScreen.name
            composable("$dailySummaryDetailedChartScreenName/{transactions}", arguments = listOf(navArgument("transactions"){type = NavType.StringType})){
                backStackEntry ->
                val transactionsJson = backStackEntry.arguments?.getString("transactions")
                val listType = object: TypeToken<List<TransactionModelParcelable>>(){}.type
                val transactions: List<TransactionModelParcelable>? = Gson().fromJson(transactionsJson, listType)
                if (transactions != null) {
                    DailySummaryDetailedChartScreen(navController = navController, transactions = transactions)
                }

            }

            composable(PlannerScreens.RecurringTransactionsScreen.name) {
                PlannerRecurringTransactionsScreen(
                    navController = navController,
                )
            }

        }


    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentBackStackEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentBackStackEntry)
}