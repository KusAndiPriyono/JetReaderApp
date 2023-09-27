package com.example.jetreaderapp.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetreaderapp.data.AuthRepository
import com.example.jetreaderapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    val _signUpState = Channel<SignUpState>()
    val signUpState = _signUpState.receiveAsFlow()


    fun registerUser(firstName: String, lastName: String, email: String, password: String) =
        viewModelScope.launch {
            repository.registerUser(firstName, lastName, email, password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _signUpState.send(SignUpState(isSuccess = "Registration Successful"))
                    }

                    is Resource.Loading -> {
                        _signUpState.send(SignUpState(isLoading = true))
                    }

                    is Resource.Error -> {
                        _signUpState.send(SignUpState(isError = result.message))
                    }
                }
            }
        }
}


