package com.example.planificatorbuget.screens.accountsettings

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.planificatorbuget.R
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.EmailInput
import com.example.planificatorbuget.components.InputField
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.components.PasswordInput
import com.example.planificatorbuget.model.UserModel
import com.example.planificatorbuget.navigation.FunctionalitiesRoutes
import com.example.planificatorbuget.screens.SharedViewModel
import com.example.planificatorbuget.utils.gradientBackgroundBrush
import com.google.firebase.auth.FirebaseAuth

@Preview
@Composable
fun PlannerAccountSettingsScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: SharedViewModel = hiltViewModel()
) {

    val dataOrException by viewModel.data.observeAsState()
    val user = dataOrException?.data
    val isLoading = dataOrException?.isLoading ?: true

    Log.d("PlannerAccountScreenSettings", "PlannerAccountScreen: ${user.toString()}")
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
                    title = "Setari cont",
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
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(bottom = 15.dp, start = 15.dp, end = 15.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(150.dp)

                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize(),
                                shape = CircleShape,
                                shadowElevation = 5.dp,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.profil_avatar),
                                    contentDescription = "Profile picture",
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clickable { }
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit profile picture",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.BottomEnd)
                                    .background(Color.Black, shape = CircleShape)
                                    .padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = user?.userName ?: "Nume utilizator",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        EditUserInfo(
                            user = user,
                            navController = navController,
                        ) { user, email, password ->
                            if (email.isNotEmpty()) {
                                viewModel.updateUserEmail(email)
                            }
                            if (password.isNotEmpty()) {
                                viewModel.updateUserPassword(password)
                            }
                            viewModel.updateUserData(user)
                            if (password.isNotEmpty() || email.isNotEmpty()) {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(FunctionalitiesRoutes.Authentication.name) {
                                    popUpTo(FunctionalitiesRoutes.Main.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditUserInfo(
    navController: NavController, user: UserModel?,
    onClick: (Map<String, Any>, email: String, password: String) -> Unit
) {
    val email = rememberSaveable {
        mutableStateOf("")
    }
    val password = rememberSaveable {
        mutableStateOf("")
    }

    val userName = rememberSaveable {
        mutableStateOf("")
    }

    val initialBudget = rememberSaveable {
        mutableStateOf("")
    }

    val passwordVisibility = rememberSaveable {
        mutableStateOf(false)
    }
    val enabledButton =
        rememberSaveable(email.value, password.value,userName.value.isNotEmpty(),initialBudget.value.isNotEmpty()) {
            email.value.isNotEmpty() || password.value.isNotEmpty() || (userName.value.isNotEmpty() || initialBudget.value.isNotEmpty())
        }
    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        EmailInput(
            emailState = email,
            label = "Editare email",
            enabled = true
        )

        PasswordInput(
            modifier = Modifier,
            passwordState = password,
            label = "Editare parola",
            enabled = true,
            passwordVisibility = passwordVisibility,
            imeAction = ImeAction.Next,
        )

        InputField(
            valueState = userName,
            labelId = "Editare nume utilizator",
            enabled = true,
            imeAction = if (user?.initialBudget == 0.0) ImeAction.Next else ImeAction.Done,
            onAction = KeyboardActions {
                if (user?.initialBudget != 0.0) {
                    keyboardController?.hide()
                } else {
                    KeyboardActions.Default
                }
            }
        )

        InputField(
            valueState = initialBudget,
            labelId = "Adaugare buget initial",
            enabled = user?.initialBudget == 0.0,
            imeAction = ImeAction.Done,
            onAction = KeyboardActions {
                keyboardController?.hide()
            }
        )
        Text(text = "Poti adauga bugetul initial o singura data", color = Color.Gray)
        Spacer(modifier = Modifier.height(15.dp))


        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                val userToUpdate = mutableMapOf<String, Any>()
                if (userName.value.isNotEmpty() && userName.value != user?.userName) {
                    userToUpdate["user_name"] = userName.value
                }
                if (initialBudget.value.isNotEmpty() && initialBudget.value != user?.initialBudget.toString()) {
                    userToUpdate["initial_budget"] = initialBudget.value.toDoubleOrNull() ?: 0.0
                }
                onClick(userToUpdate, email.value, password.value)
            }
            , enabled = enabledButton) {
                Text(text = "Salveaza")
            }

            Button(onClick = {
                navController.popBackStack()
            }) {
                Text(text = "Renunta")
            }
        }
    }
}
