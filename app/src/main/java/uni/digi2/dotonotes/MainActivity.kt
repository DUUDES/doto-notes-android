package uni.digi2.dotonotes

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.ui.BottomNavigationApp
import uni.digi2.dotonotes.ui.screens.authorization.FirebaseUIAuthScreen
import uni.digi2.dotonotes.ui.screens.tasks.TodoListScreen
import uni.digi2.dotonotes.ui.screens.tasks.TodoViewModel

class MainActivity : ComponentActivity() {

    private val todoViewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val firebaseAuth = FirebaseAuth.getInstance()

        setContent {
            val navController = rememberNavController()
            if (FirebaseAuth.getInstance().currentUser == null) {
                FirebaseUIAuthScreen(firebaseAuth = firebaseAuth,
                    signInProviders = providers,
                    onSignInSuccess = {
                        setContent {
                            BottomNavigationApp(navController)
                        }
                    },
                    onSignInFailure = { error -> throw error })
            } else {
//                TodoListScreen(todoViewModel)
                BottomNavigationApp(navController)
            }
        }
    }

}

