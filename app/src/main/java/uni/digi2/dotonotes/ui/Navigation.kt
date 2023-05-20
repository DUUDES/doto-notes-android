package uni.digi2.dotonotes.ui

import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uni.digi2.dotonotes.ui.screens.authorization.FirebaseUIAuthScreen
import uni.digi2.dotonotes.ui.screens.authorization.AuthScreen
import uni.digi2.dotonotes.ui.screens.home.HomeScreen
import uni.digi2.dotonotes.ui.screens.profile.ProfileScreen
import uni.digi2.dotonotes.ui.screens.profile.signOut
import uni.digi2.dotonotes.ui.screens.tasks.TodoListScreen
import uni.digi2.dotonotes.ui.screens.tasks.TodoViewModel


@Composable
fun AppNavHost(navController: NavController) {

    NavHost(
        navController = navController as NavHostController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onSignOut = { signOut(navController) })
        }
        composable(Screen.Tasks.route) {
            TodoListScreen()
        }
        composable("auth") {
            AuthScreen(navController)
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
        Screen.Profile
    )

    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold(
            bottomBar = {
                BottomNavigation(
//                    backgroundColor = Color.Black, // Задаємо чорний фон для BottomNavigation
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
    object Tasks : Screen("tasks", "Завдання", Icons.Filled.Check)
    object Profile : Screen("profile", "Профіль", Icons.Default.Person)
}
