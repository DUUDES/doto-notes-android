package uni.digi2.dotonotes.ui

import android.telecom.Call.Details
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uni.digi2.dotonotes.data.tasks.TaskRepository
import uni.digi2.dotonotes.data.tasks.TodoTasksDao
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.R
import uni.digi2.dotonotes.data.categories.CategoriesDao
import uni.digi2.dotonotes.ui.screens.authorization.FirebaseUIAuthScreen
import uni.digi2.dotonotes.ui.screens.authorization.AuthScreen
import uni.digi2.dotonotes.ui.screens.categories.CategoriesListScreen
import uni.digi2.dotonotes.ui.screens.home.HomeScreen
import uni.digi2.dotonotes.ui.screens.profile.ProfileScreen
import uni.digi2.dotonotes.ui.screens.tasks.CompletedTasksScreen
import uni.digi2.dotonotes.ui.screens.tasks.TaskDetailsScreen
import uni.digi2.dotonotes.ui.screens.tasks.TodoListScreen
import uni.digi2.dotonotes.ui.screens.tasks.TodoViewModel

val viewModel: TodoViewModel = TodoViewModel(TaskRepository(TodoTasksDao()), CategoriesDao())
@Composable
fun AppNavHost(navController: NavController) {

    NavHost(
        navController = navController as NavHostController,
        startDestination = Screen.Tasks.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onSignOut = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Auth.route)
            })
        }
        composable(Screen.Tasks.route) {
            TodoListScreen(navController)
        }
        composable(Screen.Categories.route) {
            CategoriesListScreen()
        }
        composable(Screen.CompletedTasks.route) {
            CompletedTasksScreen(navController, viewModel)
        }
        composable(Screen.Auth.route) {
            AuthScreen(navController)
        }
        composable(
            route = "task_details/{task_id}",
            arguments = listOf(navArgument("task_id"){type = NavType.Companion.StringType })
        ) { backstack ->
            val taskId = backstack.arguments!!.getString("task_id").toString()
            FirebaseAuth.getInstance().currentUser?.let {
                viewModel.getTaskById(it.uid, taskId)?.let {task ->
                    TaskDetailsScreen(task = task)
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
        Screen.Home,
        Screen.Tasks,
        Screen.CompletedTasks,
        Screen.Profile
    )

    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold(

            bottomBar = {
                BottomNavigation(
                     // Задаємо чорний фон для BottomNavigation
                    contentColor = Color.White // Задаємо білий колір контенту (тексту та іконок)
                ) {
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
                            selectedContentColor = Color.White// Задаємо колір вибраного контенту
//                            unselectedContentColor = Color.Gray, // Задаємо колір невибраного контенту
//                            modifier = Modifier.background(Color.Black) // Задаємо чорний фон для кожного пункту
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(Color.White) // Задаємо білий фон для контенту
            ) {
                AppNavHost(navController = navController)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Головна", Icons.Default.Home)
    object Tasks : Screen("tasks", "Завдання", Icons.Filled.List)
//    object TaskDetails : Screen("task_details", "Детальніше", Icons.Filled.Info)
    object CompletedTasks : Screen("completedTasks", "Виконані", Icons.Filled.Done)
    object Profile : Screen("profile", "Профіль", Icons.Default.Person)
    object Auth : Screen("auth", "Авторизація", Icons.Default.Home)
    object Categories : Screen("categories", "Категорії", Icons.Default.List)
}
