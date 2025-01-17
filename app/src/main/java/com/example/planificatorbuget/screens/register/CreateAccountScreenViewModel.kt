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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateAccountScreenViewModel: ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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
                                _errorMessage.value = "A apărut o eroare: ${task.exception?.message}"
                            }
                            _loading.value = false
                        }
                } catch (e: Exception) {
                    Log.d("CreateAccountScreenViewModel", "Error: ${e.message}")
                }
            }

        }
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid

        val user = UserModel(
            userId = userId.toString(),
            userName = displayName.toString(),
            initialBudget = 0.0,
            currentBudget = 0.0,
            avatarUrl = ""
        ).toMap()

        val db = FirebaseFirestore.getInstance()
        val dbCollection = db.collection("users").document(userId.toString())

        dbCollection.set(user)
            .addOnSuccessListener {
                Log.d("CreateAccountScreenViewModel", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("CreateAccountScreenViewModel", "Error writing document", e)
            }
    }
}