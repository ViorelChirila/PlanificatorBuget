package com.example.planificatorbuget.screens.categories

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.planificatorbuget.components.AppBar
import com.example.planificatorbuget.components.FABContent
import com.example.planificatorbuget.components.NavigationBarComponent
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.screens.SharedViewModel
import com.example.planificatorbuget.utils.gradientBackgroundBrush

@Preview
@Composable
fun PlannerCategoriesScreen(
    navController: NavController = NavController(LocalContext.current),
    sharedViewModel: SharedViewModel = hiltViewModel(),
    categoriesSharedViewModel: CategoriesScreenViewModel = hiltViewModel()
) {

    val categoriesData by categoriesSharedViewModel.categories.collectAsState()
    val categories by remember {
        derivedStateOf { categoriesData.data ?: emptyList() }
    }

    val resultForAdd by categoriesSharedViewModel.categoryAddResult.observeAsState()

    val icons by sharedViewModel.icons.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val showLoading = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.background(
            brush = gradientBackgroundBrush(
                isVerticalGradient = true,
                colors = if (!isSystemInDarkTheme()) listOf(
                    Color(0xFF7F9191),
                    Color(0xffc3c3d8),
                    Color(0xff00d4ff)
                ) else listOf(
                    Color(0xFF332D2D),
                    Color(0xFF232D52),
                    Color(0xFF1442A0)
                )
            )
        )
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Adauga categorie",
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
            containerColor = Color.Transparent,
            floatingActionButton = {
                FABContent {
                    showDialog = true
                }
            }
        ) { paddingValues ->
            if (categoriesData.isLoading == true) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Loading categories...")
                    CircularProgressIndicator()
                }
            }
            else {
                Card(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(bottom = 10.dp, start = 7.dp, end = 7.dp)
                        .fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(7.dp)
                    ) {
                        items(items = categories) { category ->
                            CategoryItem(category = category)
                        }
                    }


                }
            }

        }

    }
    if (showDialog) {
        AddNewCategoryDialog(
            showLoading = showLoading,
            icons = icons,
            onDismissRequest = { showDialog = false },
            onCategoryAdded = { name, icon ->
                val category = TransactionCategoriesModel(
                    userId = "",
                    categoryName = name,
                    categoryIcon = icon
                )
                categoriesSharedViewModel.addCategory(category)
                showDialog = false
            }
        )
    }
    if (resultForAdd is Response.Loading){
        showLoading.value = true
        Toast.makeText(context, "Se adauga categoria...", Toast.LENGTH_SHORT).show()
    }
    else if (resultForAdd is Response.Success && (resultForAdd as Response.Success).data == true){
        Toast.makeText(context, "Categoria adaugata cu succes", Toast.LENGTH_SHORT).show()
        showLoading.value = false
        categoriesSharedViewModel.fetchCategoriesFromFirebase()
    }
    else if (resultForAdd is Response.Error){
        Toast.makeText(context, (resultForAdd as Response.Error).message, Toast.LENGTH_SHORT).show()
    }

}

@Composable
fun CategoryItem(category: TransactionCategoriesModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Surface(shape = CircleShape) {
                Log.d("CategoryItem", "categoryIcon: ${category.categoryIcon}")
                AsyncImage(
                    model = category.categoryIcon.toUri(),
                    contentDescription = "icon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = category.categoryName,
                modifier = Modifier.padding(5.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun IconSelectionDialog(
    icons: List<String>,
    onIconSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Alege o imagine", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(icons.size) { index ->
                        Image(
                            painter = rememberAsyncImagePainter(icons[index]),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(60.dp)
                                .clickable {
                                    onIconSelected(icons[index])
                                    onDismissRequest()
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddNewCategoryDialog(
    showLoading: MutableState<Boolean>,
    icons: List<String>,
    onDismissRequest: () -> Unit,
    onCategoryAdded: (String, String) -> Unit
) {
    val showIconSelectDialog = remember { mutableStateOf(false) }
    var selectedIcon by remember { mutableStateOf<String?>(null) }
    var categoryName by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            if (showLoading.value) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()

                }
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Adauga o categorie",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text(text = "Nume categorie") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Alege o imagine")
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier.size(100.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        AsyncImage(
                            model = selectedIcon?.toUri(),
                            contentDescription = "icon",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { showIconSelectDialog.value = true }
                        )
                    }
                    if (showIconSelectDialog.value) {
                        IconSelectionDialog(
                            icons = icons,
                            onIconSelected = { icon ->
                                selectedIcon = icon
                                showIconSelectDialog.value = false
                            },
                            onDismissRequest = { showIconSelectDialog.value = false }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        TextButton(onClick = {
                            onDismissRequest()
                        }) {
                            Text(text = "Renunta")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (categoryName.isEmpty() || selectedIcon == null) {
                                Toast.makeText(
                                    context,
                                    "Completeaza toate campurile",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                onCategoryAdded(categoryName, selectedIcon ?: "")
                                onDismissRequest()

                            }
                        }) {
                            Text(text = "Adauga")
                        }
                    }
                }
            }
        }

    }
}
