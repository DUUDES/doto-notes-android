package uni.digi2.dotonotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.ui.DoToApplication
import uni.digi2.dotonotes.ui.screens.authorization.FirebaseUIAuthScreen
import uni.digi2.dotonotes.ui.screens.splash.SplashViewModel
import uni.digi2.dotonotes.ui.theme.DoToTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewModel = SplashViewModel()

        installSplashScreen().setKeepOnScreenCondition { viewModel.isLoading.value }

        setContent {
            DoToTheme {
                val navController = rememberNavController()
                DoToApplication(navController)
            }
        }
    }
}


