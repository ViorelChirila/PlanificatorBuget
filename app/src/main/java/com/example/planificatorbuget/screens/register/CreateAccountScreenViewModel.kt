package com.example.planificatorbuget.screens.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CreateAccountScreenViewModel: ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) =
        viewModelScope.launch {
            if (_loading.value == false) {
                _loading.value = true
                try {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(
                                    "CreateAccountScreenViewModel",
                                    "createUserWithEmailAndPassword:success"
                                )
                                val displayName = task.result?.user?.email?.split("@")?.get(0)
                                createUser(displayName)
                                home()
                            } else {
                                Log.d(
                                    "CreateAccountScreenViewModel",
                                    "createUserWithEmailAndPassword:failure"
                                )
                            }
                            _loading.value = false
                        }
                } catch (e: Exception) {
                    Log.d("CreateAccountScreenViewModel", "Error: ${e.message}")
                }
            }

        }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid

        val user = UserModel(
            userId = userId.toString(),
            userName = displayName.toString(),
            profession = "",
            initialBudget = 0.0
        ).toMap()


        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }
}