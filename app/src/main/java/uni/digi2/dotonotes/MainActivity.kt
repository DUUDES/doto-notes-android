package uni.digi2.dotonotes

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import uni.digi2.dotonotes.ui.theme.DoToNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val firebaseAuth = FirebaseAuth.getInstance()

        setContent {
            FirebaseUIAuthScreen(
                firebaseAuth = firebaseAuth,
                signInProviders = providers,
                onSignInSuccess = { /* Дії після успішної автентифікації */
                                  setContent {
                                      Greeting("Android")
                                  }},
                onSignInFailure = { error -> /* Дії після неуспішної автентифікації */ }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DoToNotesTheme {
        Greeting("Android")
    }
}


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
            // Аутентифікація успішна
            onSignInSuccess()
        } else {
            // Аутентифікація не вдалася
            val response = IdpResponse.fromResultIntent(null)
            response?.error?.let { error ->
                onSignInFailure(error)
            }
        }
    }


    val signInIntent = remember {
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setLogo(R.drawable.ic_todo_list)
            .setAvailableProviders(signInProviders)
            .build()
    }

    LaunchedEffect(Unit) {
        signInLauncher.launch(signInIntent)
    }
}



//@Composable
//private fun ShowFragment() {
//
//    if(FirebaseAuth.getInstance().currentUser == null){
//        // відкриваємо вікно логіну
//        DoLogin()
//
//
//
//    }else{
//        // відкриваємо вікно з нотатками
//        Greeting("User")
//    }
//}


//val providers = arrayListOf(
//    AuthUI.IdpConfig.GoogleBuilder().build()
//)

//@Composable
//private fun DoLogin(){
//    val signInIntent = AuthUI.getInstance()
//        .createSignInIntentBuilder()
//        .setAvailableProviders(providers)
//        .setIsSmartLockEnabled(false)
//        .build()
//}



