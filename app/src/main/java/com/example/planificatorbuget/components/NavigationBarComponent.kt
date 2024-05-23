package com.example.planificatorbuget.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import com.example.planificatorbuget.data.BottomNavigationItem
import  androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
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
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart,
            hasNews = false,
            badgeCount = null
        ),
        BottomNavigationItem(
            title = "Tranzactii",
            selectedIcon = Icons.AutoMirrored.Filled.Sort,
            unselectedIcon = Icons.AutoMirrored.Outlined.Sort,
            hasNews = false,
            badgeCount = null
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

    NavigationBar {
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