package com.example.jetreaderapp.screens.signup

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.jetreaderapp.components.login.EmailInput
import com.example.jetreaderapp.components.login.PasswordInput
import com.example.jetreaderapp.components.login.SubmitButton
import com.example.jetreaderapp.components.signup.FirstNameInput
import com.example.jetreaderapp.components.signup.ImageSignUp
import com.example.jetreaderapp.components.signup.LastNameInput
import com.example.jetreaderapp.navigation.ReaderScreens
import com.example.jetreaderapp.ui.theme.Purple40

@Composable
fun ReaderSignUpScreen(
    navController: NavController,
    viewModel: SignUpScreenViewModel = hiltViewModel()
) {

    val state = viewModel.signUpState.collectAsState(initial = SignUpState()).value
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ImageSignUp()
            UserForm(
                loading = false,
                onDone = { firstName, lastName, email, password ->
                    viewModel.registerUser(firstName, lastName, email, password) {
                        navController.navigate(ReaderScreens.LoginScreen.name)
                    }
                }
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.padding(15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text = "Login"
                Text(text = "Already have an account?")
                Text(
                    text,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.LoginScreen.name)
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
    onDone: (String, String, String, String) -> Unit = { firstName, lastName, email, password -> }
) {
    val firstName = rememberSaveable { mutableStateOf("") }
    val lastName = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(firstName.value, lastName.value, email.value, password.value) {
        firstName.value.trim().isNotBlank() &&
                lastName.value.trim().isNotBlank() &&
                email.value.trim().isNotBlank() &&
                password.value.trim().isNotBlank()
    }

    val cardSignUpModifier = Modifier
        .fillMaxWidth()
        .padding(top = 0.dp, start = 20.dp, end = 20.dp, bottom = 0.dp)
        .background(MaterialTheme.colorScheme.background)

    Card(
        modifier = cardSignUpModifier,
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
            FirstNameInput(
                firstNameState = firstName,
                enabled = !loading,
                onAction = KeyboardActions {
                    passwordFocusRequest.requestFocus()
                })
            LastNameInput(lastNameState = lastName, enabled = !loading, onAction = KeyboardActions {
                passwordFocusRequest.requestFocus()
            })
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
                    onDone(
                        firstName.value.trim(),
                        lastName.value.trim(),
                        email.value.trim(),
                        password.value.trim()
                    )
                }
            )
            SubmitButton(
                textId = "Submit",
                loading = loading,
                validInputs = valid
            ) {
                onDone(
                    firstName.value.trim(),
                    lastName.value.trim(),
                    email.value.trim(),
                    password.value.trim()
                )
                keyboardController?.hide()
            }
        }
    }
}