package uni.digi2.dotonotes.ui.screens.authorization

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import uni.digi2.dotonotes.R
import uni.digi2.dotonotes.ui.Screen

val authProviders = listOf(
    AuthUI.IdpConfig.EmailBuilder().build(),
    AuthUI.IdpConfig.GoogleBuilder().build()
)

@Composable
fun AuthScreen(navController: NavController) {

    FirebaseUIAuthScreen(
        signInProviders = authProviders,
        onSignInSuccess = {
            navController.navigate(Screen.Tasks.route)
        },
        onSignInFailure = { error -> throw error }
    )
}

//@Composable
//fun FirebaseUIAuthScreen(
//    signInProviders: List<AuthUI.IdpConfig>,
//    onSignInSuccess: () -> Unit,
//    onSignInFailure: (Exception) -> Unit
//) {
//    val context = LocalContext.current
//    val signInLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            onSignInSuccess()
//        } else {
//            val response = IdpResponse.fromResultIntent(result.data)
//            response?.error?.let { error ->
//                onSignInFailure(error)
//            }
//        }
//    }
//
//    val signInIntent = remember(signInProviders) {
//        AuthUI.getInstance().createSignInIntentBuilder()
//            .setLogo(R.drawable.im_round_bounded)
//            .setAvailableProviders(signInProviders)
//            .build()
//    }
//
//    LaunchedEffect(Unit) {
//        signInLauncher.launch(signInIntent)
//    }
//}
@Composable
fun FirebaseUIAuthScreen(
    signInProviders: List<AuthUI.IdpConfig> = authProviders,
    onSignInSuccess: () -> Unit,
    onSignInFailure: (Exception) -> Unit
) {
    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSignInSuccess()
        } else {
            val response = IdpResponse.fromResultIntent(null)
            response?.error?.let { error ->
                onSignInFailure(error)
            }
        }
    }

    val signInIntent = remember {
        AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false)
            .setLogo(R.drawable.im_round_bounded).setAvailableProviders(signInProviders).build()
    }

    LaunchedEffect(Unit) {
        signInLauncher.launch(signInIntent)
    }
}