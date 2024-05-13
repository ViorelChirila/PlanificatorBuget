package com.example.planificatorbuget

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel(){
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady
    val user:MutableState<Boolean> = mutableStateOf(false)


    init {
        viewModelScope.launch {
            delay(3000L)
            isUserLoggedIn()
            _isReady.value = true
        }
    }

    private fun isUserLoggedIn(){
        user.value = FirebaseAuth.getInstance().currentUser != null
    }
}