package com.example.planificatorbuget.screens

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.UserModel
import com.example.planificatorbuget.repository.CategoryIconsRepository
import com.example.planificatorbuget.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val categoryIconsRepository: CategoryIconsRepository
) : ViewModel() {

    private val _data = MutableLiveData<DataOrException<UserModel, Boolean, Exception>>()
    val data: LiveData<DataOrException<UserModel, Boolean, Exception>> get() = _data

    private val _dataUpdateResult = MutableLiveData<Response<Boolean>>()
    val dataUpdateResult: LiveData<Response<Boolean>> get() = _dataUpdateResult

    private val _isUpdateDone = MutableLiveData<Boolean>()
    val isUpdateDone: LiveData<Boolean> get() = _isUpdateDone

    private val _icons = MutableStateFlow<List<String>>(emptyList())
    val icons: StateFlow<List<String>> get() = _icons

    init {
        fetchUser()
        fetchIcons()
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
            _dataUpdateResult.value = Response.Loading()
            val result = userRepository.updateUserData(user)
            _dataUpdateResult.postValue(result)
            _isUpdateDone.postValue(true)

        }
    }

    fun updateUserEmail(email: String) {
        viewModelScope.launch {
            userRepository.updateUserEmail(email)
        }
    }

    fun updateUserPassword(password: String) {
        viewModelScope.launch {
            userRepository.updateUserPassword(password)
        }
    }

    fun updateUserPhoto(photoUri: Uri, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.uploadImageToFirebaseStorage(photoUri)
            Log.d("SharedViewModel", "updateUserPhoto: ${result.data}")
            result.data?.let { onSuccess(it) }
        }
    }


    fun resetUpdateStatus() {
        _isUpdateDone.value = false // Reset isUpdateDone to false when needed
    }

    fun fetchIcons() {
        viewModelScope.launch {
            val result = categoryIconsRepository.fetchIconsFromFirestore()
            _icons.value = result
            Log.d("SharedViewModel", "fetchIcons: ${_icons.value}")
        }
    }
}