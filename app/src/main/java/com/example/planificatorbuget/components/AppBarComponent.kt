package com.example.planificatorbuget.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.planificatorbuget.navigation.PlannerScreens

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    title: String = "Planner",
    haveNotifications: Boolean = false,
    isHomeScreen: Boolean = false,
    navController: NavController = NavController(LocalContext.current),
    onBackArrowClicked: () -> Unit = {}
) {

    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            if (!isHomeScreen) {
                IconButton(onClick = { onBackArrowClicked() }) {
                    Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back")

                }
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate(PlannerScreens.NotificationsScreen.name)
            }) {
                BadgedBox(badge = {
                    if (haveNotifications) {
                        Badge(modifier = Modifier.offset(x = (-4).dp))
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }

            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
    )
}