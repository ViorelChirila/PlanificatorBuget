package com.example.planificatorbuget.screens.account

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.planificatorbuget.R
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.data.AccountOptionItem
import com.example.planificatorbuget.navigation.PlannerScreens
import com.example.planificatorbuget.utils.gradientBackgroundBrush
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PlannerAccountScreen(
    navController: NavController,
    viewModel: AccountScreenViewModel = hiltViewModel()
) {

    val dataOrException by viewModel.data.observeAsState()
    val user = dataOrException?.data
    val isLoading = dataOrException?.isLoading ?: true

    Log.d("PlannerAccountScreen", "PlannerAccountScreen: ${user.toString()}")

    Box(
        modifier = Modifier.background(
            brush = gradientBackgroundBrush(
                isVerticalGradient = true,
                colors = listOf(
                    Color(0xFF7F9191),
                    Color(0xffc3c3d8),
                    Color(0xff00d4ff)
                )
            )
        )
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Cont",
                    haveNotifications = false,
                    isHomeScreen = false,
                    navController = navController
                ) {
                    navController.popBackStack()
                }
            },
            bottomBar = {
                NavigationBarComponent(navController = navController)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Surface(
                            modifier = Modifier.padding(5.dp),
                            shape = CircleShape,
                            shadowElevation = 5.dp,
                        ) {
                            var selectedImageUri by remember {
                                mutableStateOf<Uri?>(null)
                            }

                            val photoPickerLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.PickVisualMedia(),
                                onResult = { uri -> selectedImageUri = uri }
                            )

                            ProfileImage(selectedImageUri)
                            {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = user?.userName ?: "Nume utilizator",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        AccountOptions(navController = navController)
                    }
                }


            }
        }
    }
}

@Composable
private fun ProfileImage(
    selectedImageUri: Uri?,
    onClick: () -> Unit
) {

    if (selectedImageUri == null) {
        Image(
            painter = painterResource(id = R.drawable.profil_avatar),
            contentDescription = "Profile picture",
            modifier = Modifier
                .clickable { onClick() }
                .size(150.dp)
        )
    } else {
        AsyncImage(
            model = selectedImageUri,
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clickable { onClick() }
                .size(150.dp))
    }


}

@Composable
fun AccountOptions(navController: NavController) {
    val options = listOf(
        AccountOptionItem(
            "Setari cont",
            painterResource(id = R.drawable.account_info)
        ) { /* Handle click for "Setari cont" here */ },
        AccountOptionItem(
            "Setari notificari",
            painterResource(id = R.drawable.notification_settings)
        ) { /* Handle click for "Setari notificari" here */ },
        AccountOptionItem("Deconecteaza-te", painterResource(id = R.drawable.logout_button)) {
            FirebaseAuth.getInstance().signOut().run {
                navController.navigate(PlannerScreens.LoginScreen.name)
            }
        }
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
    ) {
        LazyColumn(
            contentPadding = PaddingValues(15.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { option ->
                OptionItem(
                    name = option.name,
                    icon = option.icon,
                    onClick = option.onClick
                )
            }
        }

    }
}

@Composable
fun OptionItem(
    name: String = "Option name",
    icon: Painter,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
            Image(
                painter = icon,
                contentDescription = "Account info icon",
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = name,
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.labelMedium
            )
        }

    }
}

