package uni.digi2.dotonotes.ui

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uni.digi2.dotonotes.ui.screens.home.HomeScreen
import uni.digi2.dotonotes.ui.screens.profile.ProfileScreen
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
            ProfileScreen()
        }
        composable(Screen.Tasks.route) {
            TodoListScreen()
        }
    }
}

val LocalNavController = compositionLocalOf<NavController> { error("No NavController found") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationApp(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Profile,
        Screen.Tasks
    )

    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold(
            bottomBar = {
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
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = onClick
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AppNavHost(navController = navController)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Головна сторінка", Icons.Default.Home)
    object Tasks : Screen("tasks", "Завдання", Icons.Filled.Check)
    object Profile : Screen("profile", "Профіль", Icons.Default.Person)
}
