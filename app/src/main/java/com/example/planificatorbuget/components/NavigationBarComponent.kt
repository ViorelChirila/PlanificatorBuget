package com.example.planificatorbuget.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AreaChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.planificatorbuget.data.BottomNavigationItem
import com.example.planificatorbuget.navigation.PlannerScreens

@Composable
fun NavigationBarComponent(navController: NavController) {

    val items = listOf(
        BottomNavigationItem(
            title = "Acasa",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            badgeCount = null,
            destination = PlannerScreens.HomeScreen.name
        ),
        BottomNavigationItem(
            title = "Statistici",
            selectedIcon = Icons.Filled.AreaChart,
            unselectedIcon = Icons.Outlined.AreaChart,
            hasNews = false,
            badgeCount = null,
            destination = PlannerScreens.StatisticsScreen.name
        ),
        BottomNavigationItem(
            title = "Tranzactii",
            selectedIcon = Icons.Filled.MonetizationOn,
            unselectedIcon = Icons.Outlined.MonetizationOn,
            hasNews = false,
            badgeCount = null,
            destination = PlannerScreens.TransactionsScreen.name+"/ "
        ),
        BottomNavigationItem(
            title = "Cont",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
            hasNews = false,
            badgeCount = null,
            destination = PlannerScreens.AccountScreen.name
        )

    )

    NavigationBar(modifier = Modifier.clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) {
        items.forEach {item ->
            val isSelected = item.destination == navController.currentDestination?.route
            NavigationBarItem(selected = isSelected,
                onClick = {
                    item.destination?.let { navController.navigate(it) }
                },
                label = {
                    Text(text = item.title)
                },
                icon = {
                    BadgedBox(badge = {
                        if (item.badgeCount != null) {
                            Badge() {
                                Text(text = item.badgeCount.toString())
                            }
                        } else if (item.hasNews) {
                            Badge()
                        }
                    }) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                })
        }
    }
}