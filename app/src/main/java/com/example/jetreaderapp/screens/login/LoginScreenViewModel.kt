package com.example.jetreaderapp.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    //    val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun loginWithEmailAndPassword(email: String, password: String, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmailAndPassword: ${task.result}")
                            home()
                        } else {
                            Log.d(TAG, "signInWithEmailAndPassword: ${task.result}")
                        }
                    }
            } catch (e: Exception) {
                Log.d(TAG, "signInWithEmailAndPassword: ${e.message}")
            }
        }

    fun firebaseAuthWithGoogle(idToken: String, home: () -> Unit) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential: ${task.result}")
                    home()
                } else {
                    Log.d(TAG, "signInWithCredential: ${task.result}")
                }
            }
    }


    companion object {
        private const val TAG = "Login"
    }
}