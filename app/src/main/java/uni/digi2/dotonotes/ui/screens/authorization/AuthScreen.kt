package uni.digi2.dotonotes.ui.screens.authorization

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.R
import uni.digi2.dotonotes.ui.BottomNavigationApp
import uni.digi2.dotonotes.ui.Screen
import uni.digi2.dotonotes.ui.utils.Greeting

@Composable
fun AuthScreen(navController: NavController) {
    val providers = listOf(
        AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
    )

    FirebaseUIAuthScreen(
        signInProviders = providers,
        onSignInSuccess = {
            navController.navigate(Screen.Home.route)
        },
        onSignInFailure = { error -> throw error }
    )
}

@Composable
fun FirebaseUIAuthScreen(
    signInProviders: List<AuthUI.IdpConfig>,
    onSignInSuccess: () -> Unit,
    onSignInFailure: (Exception) -> Unit
) {
    val context = LocalContext.current
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSignInSuccess()
        } else {
            val response = IdpResponse.fromResultIntent(result.data)
            response?.error?.let { error ->
                onSignInFailure(error)
            }
        }
    }

    val signInIntent = remember(signInProviders) {
        AuthUI.getInstance().createSignInIntentBuilder()
            .setIsSmartLockEnabled(true) // Consider enabling Smart Lock if desired
            .setLogo(R.drawable.ic_todo_list) // Make sure the resource exists and is correctly referenced
            .setAvailableProviders(signInProviders)
            .build()
    }

    LaunchedEffect(Unit) {
        signInLauncher.launch(signInIntent)
    }
}