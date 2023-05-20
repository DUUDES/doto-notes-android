package uni.digi2.dotonotes.ui.screens.authorization

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.R

@Composable
fun FirebaseUIAuthScreen(
    firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    signInProviders: List<AuthUI.IdpConfig>,
    onSignInSuccess: () -> Unit,
    onSignInFailure: (Exception) -> Unit
) {
    val context = LocalContext.current
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
            .setLogo(R.drawable.ic_todo_list).setAvailableProviders(signInProviders).build()
    }

    LaunchedEffect(Unit) {
        signInLauncher.launch(signInIntent)
    }
}
