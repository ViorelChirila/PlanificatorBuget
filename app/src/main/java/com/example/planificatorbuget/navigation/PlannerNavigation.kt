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
import com.example.planificatorbuget.model.TransactionModelParcelable
import com.example.planificatorbuget.screens.SharedViewModel
import com.example.planificatorbuget.screens.account.PlannerAccountScreen
import com.example.planificatorbuget.screens.accountsettings.PlannerAccountSettingsScreen
import com.example.planificatorbuget.screens.categories.CategoriesScreenViewModel
import com.example.planificatorbuget.screens.categories.PlannerCategoriesScreen
import com.example.planificatorbuget.screens.chartdetailsscreeens.BudgetEvolutionDetailedChartScreen
import com.example.planificatorbuget.screens.chartdetailsscreeens.DailySummaryDetailedChartScreen
import com.example.planificatorbuget.screens.chartdetailsscreeens.FinancialFluxDetailedScreen
import com.example.planificatorbuget.screens.csvuploadscreen.CsvUploadScreen
import com.example.planificatorbuget.screens.home.PlannerHomeScreen
import com.example.planificatorbuget.screens.login.PlannerLoginScreen
import com.example.planificatorbuget.screens.notification.PlannerNotificationsScreenSettings
import com.example.planificatorbuget.screens.recurringtransactions.PlannerRecurringTransactionsScreen
import com.example.planificatorbuget.screens.register.PlannerCreateAccountScreen
import com.example.planificatorbuget.screens.statistics.PlannerStatisticsScreen
import com.example.planificatorbuget.screens.transactiondetailsscreen.PlannerTransactionDetailsScreen
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

            composable(PlannerScreens.NotificationsScreenSettings.name) {
                PlannerNotificationsScreenSettings(navController = navController)
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
                val viewModel = it.sharedViewModel<SharedViewModel>(navController)
                PlannerStatisticsScreen(navController = navController, sharedViewModel = viewModel)
            }


            val transactionName = PlannerScreens.TransactionsScreen.name
            composable(
                "$transactionName/{selectedDate}",
                arguments = listOf(navArgument("selectedDate") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
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

            val dailySummaryDetailedChartScreenName =
                PlannerScreens.DailySummaryDetailedChartScreen.name
            composable(
                "$dailySummaryDetailedChartScreenName/{transactions}",
                arguments = listOf(navArgument("transactions") { type = NavType.StringType })
            ) { backStackEntry ->
                val transactionsJson = backStackEntry.arguments?.getString("transactions")
                val listType = object : TypeToken<List<TransactionModelParcelable>>() {}.type
                val transactions: List<TransactionModelParcelable>? =
                    Gson().fromJson(transactionsJson, listType)
                if (transactions != null) {
                    DailySummaryDetailedChartScreen(
                        navController = navController,
                        transactions = transactions
                    )
                }

            }

            composable(PlannerScreens.RecurringTransactionsScreen.name) {
                PlannerRecurringTransactionsScreen(
                    navController = navController,
                )
            }

            val transactionDetailsScreenName = PlannerScreens.TransactionDetailsScreen.name
            composable(
                "$transactionDetailsScreenName/{transactionId}",
                arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId")
                PlannerTransactionDetailsScreen(
                    navController = navController,
                    transactionId = transactionId
                )
            }

            val financialFluxDetailedChartScreenName =
                PlannerScreens.FinancialFluxDetailedChartScreen.name
            composable(
                "$financialFluxDetailedChartScreenName/{transactions}",
                arguments = listOf(navArgument("transactions") { type = NavType.StringType })
            ) { backStackEntry ->
                val transactionsJson = backStackEntry.arguments?.getString("transactions")
                val listType = object : TypeToken<List<TransactionModelParcelable>>() {}.type
                val transactions: List<TransactionModelParcelable>? =
                    Gson().fromJson(transactionsJson, listType)
                if (transactions != null) {
                    FinancialFluxDetailedScreen(
                        navController = navController,
                        transactions = transactions
                    )
                }
            }

            val budgetEvolutionDetailedChartScreenName = PlannerScreens.BudgetEvolutionDetailedChartScreen.name
            composable(
                "$budgetEvolutionDetailedChartScreenName/{transactions}/{initialBudget}",
                arguments = listOf(
                    navArgument("transactions") { type = NavType.StringType },
                    navArgument("initialBudget") { type = NavType.FloatType } // NavType.DoubleType is not available, use FloatType and cast to Double
                )
            ) { backStackEntry ->
                val transactionsJson = backStackEntry.arguments?.getString("transactions")
                val initialBudget = backStackEntry.arguments?.getFloat("initialBudget")?.toDouble() // Retrieve and cast to Double
                val listType = object : TypeToken<List<TransactionModelParcelable>>() {}.type
                val transactions: List<TransactionModelParcelable>? =
                    Gson().fromJson(transactionsJson, listType)
                if (transactions != null && initialBudget != null) {
                    BudgetEvolutionDetailedChartScreen(
                        navController = navController,
                        transactions = transactions,
                        initialBudget = initialBudget
                    )
                }
            }

            composable(PlannerScreens.CsvUploadScreen.name) {
                CsvUploadScreen(navController = navController)
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