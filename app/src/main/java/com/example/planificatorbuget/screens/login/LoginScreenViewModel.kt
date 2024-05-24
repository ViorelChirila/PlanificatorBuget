package com.example.planificatorbuget.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel: ViewModel(){

    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginScreenViewModel", "signInWithEmailAndPassword:success")
                            home()
                        } else {
                            Log.d("LoginScreenViewModel", "signInWithEmailAndPassword:failure")
                            _errorMessage.value = "Incorrect email or password"
                        }
                    }
            } catch (e: Exception) {
                Log.d("LoginScreenViewModel", "Error: ${e.message}")
                _errorMessage.value = "An error occurred: ${e.message}"
            }
        }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

}