package uni.digi2.dotonotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.ui.BottomNavigationApp
import uni.digi2.dotonotes.ui.screens.authorization.FirebaseUIAuthScreen

class MainActivity : ComponentActivity() {

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
                BottomNavigationApp(navController)
            }
        }
    }
}
