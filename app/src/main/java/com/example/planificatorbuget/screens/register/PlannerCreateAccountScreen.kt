package com.example.planificatorbuget.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.planificatorbuget.R
import com.example.planificatorbuget.components.UserForm
import com.example.planificatorbuget.navigation.FunctionalitiesRoutes
import com.example.planificatorbuget.navigation.PlannerScreens


@Preview
@Composable
fun PlannerCreateAccountScreen(navController: NavController = NavController(LocalContext.current),viewModel: CreateAccountScreenViewModel = viewModel()){
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.l1), contentDescription = "Logo",
                modifier = Modifier
                    .padding(30.dp)
                    .size(200.dp)
            )
            Text(
                text = "Bine ai venit!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(3.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Creaza-ti un cont.", modifier = Modifier.padding(3.dp))
            Spacer(modifier = Modifier.height(5.dp))
            UserForm(loading = false,isCreateAccountForm = true){
                email, password ->
                viewModel.createUserWithEmailAndPassword(email, password){
                    navController.navigate(FunctionalitiesRoutes.Main.name){
                        popUpTo(FunctionalitiesRoutes.Authentication.name){
                            inclusive = true
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = "Ai deja un cont?")
                Text(
                    text = "Logheaaza-te",
                    modifier = Modifier
                        .clickable {

                            navController.navigate(PlannerScreens.LoginScreen.name)
                        }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

        }

    }
}