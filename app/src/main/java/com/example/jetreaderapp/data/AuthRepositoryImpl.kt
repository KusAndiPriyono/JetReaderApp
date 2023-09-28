package com.example.jetreaderapp.data

import android.util.Log
import com.example.jetreaderapp.utils.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    ) : AuthRepository {
    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            if (!isValidEmail(email)) {
                emit(Resource.Error("Invalid email format"))
                return@flow
            }
            if (!isStrongPassword(password)) {
                emit(Resource.Error("Password must be at least 6 characters long"))
                return@flow
            }
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun registerUser(
        firstName: String,
        lastName: String, email: String, password: String
    ): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            if (!isValidEmail(email)) {
                emit(Resource.Error("Invalid email format"))
                return@flow
            }
            if (!isStrongPassword(password)) {
                emit(Resource.Error("Password must be at least 6 characters long"))
                return@flow
            }
            try {
                val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName")
                    .build()
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                firebaseAuth.currentUser?.updateProfile(userProfileChangeRequest)?.await()
                saveUserData(result.user?.uid!!, firstName, lastName, email, password).collect {}
                emit(Resource.Success(result))
            } catch (e: Exception) {
                Log.d("FB", "createAccount: ${e.message}")
                emit(Resource.Error(e.message.toString()))
            }
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun googleLogin(credential: AuthCredential): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithCredential(credential).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override suspend fun saveUserData(
        userId: String,
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            val user = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email,
                "password" to password
            )
            fireStore.collection("users").document(userId).set(user).await()
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length >= 6
    }
}