package uni.digi2.dotonotes.ui

import android.telecom.Call.Details
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Colors
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.Coil
import coil.util.CoilUtils
import uni.digi2.dotonotes.data.tasks.TodoTasksDao
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uni.digi2.dotonotes.R
import uni.digi2.dotonotes.data.categories.CategoriesDao
import uni.digi2.dotonotes.ui.screens.authorization.FirebaseUIAuthScreen
import uni.digi2.dotonotes.ui.screens.authorization.AuthScreen
import uni.digi2.dotonotes.ui.screens.categories.CategoriesListScreen
import uni.digi2.dotonotes.ui.screens.categories.TaskCategoriesViewModel
import uni.digi2.dotonotes.ui.screens.home.HomeScreen
import uni.digi2.dotonotes.ui.screens.profile.ProfileScreen
import uni.digi2.dotonotes.ui.screens.tasks.CompletedTasksScreen
import uni.digi2.dotonotes.ui.screens.tasks.TaskDetailsScreen
import uni.digi2.dotonotes.ui.screens.tasks.TasksOrderBy
import uni.digi2.dotonotes.ui.screens.tasks.TodoListScreen
import uni.digi2.dotonotes.ui.screens.tasks.TodoViewModel
import uni.digi2.dotonotes.ui.screens.tasksWithCategories.GroupedTasks
import uni.digi2.dotonotes.ui.theme.DoToTheme

@Composable
fun AppNavHost(navController: NavController) {

    val tasksDao = TodoTasksDao()
    val categoriesDao = CategoriesDao()

    val tasksViewModel = TodoViewModel(tasksDao, categoriesDao)
    val categoriesViewModel = TaskCategoriesViewModel(categoriesDao)

    NavHost(
        navController = navController as NavHostController,
        startDestination = Screen.Tasks.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onSignOut = {
                tasksViewModel.stopObservation()
                categoriesViewModel.stopObservation()

                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Auth.route)
            },
            onDeleteAccount = {
                FirebaseAuth.getInstance().currentUser?.let { it1 ->
                    tasksViewModel.deleteAllTasks(it1.uid)
                }

                tasksViewModel.stopObservation()
                categoriesViewModel.stopObservation()

                FirebaseAuth.getInstance().currentUser?.delete()
                navController.navigate(Screen.Auth.route)
            })
        }
        composable(Screen.GroupedTasks.route) {
            GroupedTasks(navController, tasksViewModel)
        }
        composable(Screen.Tasks.route) {
            TodoListScreen(navController, tasksViewModel)
        }
        composable(Screen.Categories.route) {
            CategoriesListScreen(categoriesViewModel)
        }
        composable(Screen.CompletedTasks.route) {
            CompletedTasksScreen(navController, tasksViewModel)
        }
        composable(Screen.Auth.route) {
            AuthScreen(navController)
        }
        composable(
            route = "task_details/{task_id}",
            arguments = listOf(navArgument("task_id") { type = NavType.Companion.StringType })
        ) { backstack ->
            val taskId = backstack.arguments!!.getString("task_id").toString()
            FirebaseAuth.getInstance().currentUser?.let {
                tasksViewModel.getTaskById(it.uid, taskId)?.let { task ->
                    TaskDetailsScreen(task = task, viewModel = tasksViewModel)
                }
            }
        }


    }
}

val LocalNavController = compositionLocalOf<NavController> { error("No NavController found") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationApp(navController: NavController) {
    val items = listOf(
//        Screen.Home,
        Screen.Tasks,
        Screen.GroupedTasks,
        Screen.CompletedTasks,
        Screen.Profile,
    )

    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold(
            topBar = {
            },
            bottomBar = {
                DoToTheme {
                    BottomNavigation {
                        val currentRoute = LocalNavController.current.currentDestination?.route

                        items.forEach { screen ->
                            val selected = currentRoute == screen.route
                            val onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            }

                            BottomNavigationItem(
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (selected) Color.Gray else Color.White // Задаємо колір іконки
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        color = if (selected) Color.Gray else Color.White // Задаємо колір тексту
                                    )
                                },
                                selected = selected,
                                onClick = onClick,
                                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.surface)

            ) {
                AppNavHost(navController = navController)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Головна", Icons.Default.Home)
    object Tasks : Screen("tasks", "Завдання", Icons.Default.Home)
    object GroupedTasks : Screen("grouped-tasks", "Групи", Icons.Filled.List)
    object CompletedTasks : Screen("completedTasks", "Виконані", Icons.Filled.Done)
    object Profile : Screen("profile", "Профіль", Icons.Default.Person)
    object Auth : Screen("auth", "Авторизація", Icons.Default.Home)
    object Categories : Screen("categories", "Категорії", Icons.Default.List)
}
