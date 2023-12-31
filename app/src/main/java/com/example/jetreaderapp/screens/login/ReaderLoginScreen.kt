package com.example.jetreaderapp.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.jetreaderapp.R
import com.example.jetreaderapp.components.login.EmailInput
import com.example.jetreaderapp.components.login.ImageLogin
import com.example.jetreaderapp.components.login.PasswordInput
import com.example.jetreaderapp.components.login.SubmitButton
import com.example.jetreaderapp.navigation.ReaderScreens
import com.example.jetreaderapp.ui.theme.Purple40
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun ReaderLoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    val googleLoginState = viewModel.googleState.value
    val context = LocalContext.current
    val state = viewModel.loginState.collectAsState(initial = LoginState()).value
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val result = account.getResult(ApiException::class.java)
                val credentials = GoogleAuthProvider.getCredential(result.idToken, null)
                viewModel.googleLogin(credentials) {
                    navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    val serverClientId = "YOUR_SERVER_CLIENT_ID"
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(serverClientId)
        .build()
    val googleLoginClient = GoogleSignIn.getClient(context, gso)
    Surface() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ImageLogin()
            UserForm(
                loading = false,
                onDone = { email, password ->
                    viewModel.loginUser(email, password) {
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                },
                onGoogleLogin = {
                    launcher.launch(googleLoginClient.signInIntent)
                    viewModel.googleLogin(GoogleAuthProvider.getCredential("token", null)) {
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (googleLoginState.loading) {
                    CircularProgressIndicator()
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.padding(15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text = "Create Account"
                Text(text = "Don't have an account?")
                Text(
                    text,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.CreateAccountScreen.name)
                        }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = Purple40
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
fun UserForm(
    loading: Boolean = false,
    onDone: (String, String) -> Unit = { email, password -> },
    onGoogleLogin: () -> Unit = {}
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }
    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
        .background(MaterialTheme.colorScheme.background)
//        .verticalScroll(rememberScrollState())


    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmailInput(emailState = email, enabled = !loading, onAction = KeyboardActions {
                passwordFocusRequest.requestFocus()
            })
            PasswordInput(
                modifier = Modifier.focusRequester(passwordFocusRequest),
                passwordState = password,
                labelId = "Password",
                enabled = !loading,
                passwordVisibility = passwordVisibility,
                onAction = KeyboardActions {
                    if (!valid) return@KeyboardActions
                    onDone(email.value.trim(), password.value.trim())
                }
            )
            SubmitButton(
                textId = "Login",
                loading = loading,
                validInputs = valid
            ) {
                onDone(email.value.trim(), password.value.trim())
                keyboardController?.hide()
            }
            Row(
                modifier = Modifier.padding(15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    thickness = 1.dp,
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "OR")
                Spacer(modifier = Modifier.width(5.dp))
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    thickness = 1.dp,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                IconButton(onClick = {
                    onGoogleLogin()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}







