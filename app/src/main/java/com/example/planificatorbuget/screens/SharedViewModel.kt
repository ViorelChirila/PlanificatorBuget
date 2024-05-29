package com.example.planificatorbuget.screens

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.model.UserModel
import com.example.planificatorbuget.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _data = MutableLiveData<DataOrException<UserModel, Boolean, Exception>>()
    val data: LiveData<DataOrException<UserModel, Boolean, Exception>> get() = _data

    private val _updateResult = MutableLiveData<DataOrException<Boolean, Boolean, Exception>>()
    val updateResult: LiveData<DataOrException<Boolean, Boolean, Exception>> get() = _updateResult

    init {
        fetchUser()
        Log.d("MyViewModel", "Instance created")
    }

    fun fetchUser() {
        viewModelScope.launch {
            _data.value = DataOrException(isLoading = true)
            val result = userRepository.fetchUser()
            _data.postValue(result)
        }
    }

    fun updateUserData(user: Map<String, Any>) {
        viewModelScope.launch {
            _updateResult.value = DataOrException(isLoading = true)
            val result = userRepository.updateUserData(user)
            _updateResult.postValue(result)
        }
    }

    fun updateUserEmail(email: String){
        viewModelScope.launch {
            userRepository.updateUserEmail(email)
        }
    }

    fun updateUserPassword(password: String){
        viewModelScope.launch {
            userRepository.updateUserPassword(password)
        }
    }

    fun updateUserPhoto(photoUri: Uri, onSuccess: (String) -> Unit){
        viewModelScope.launch {
            val result = userRepository.uploadImageToFirebaseStorage(photoUri)
            Log.d("SharedViewModel", "updateUserPhoto: ${result.data}")
            result.data?.let { onSuccess(it) }
        }
    }
}