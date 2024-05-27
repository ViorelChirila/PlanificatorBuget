package com.example.planificatorbuget.screens.account

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
class AccountScreenViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _data = MutableLiveData<DataOrException<UserModel, Boolean, Exception>>()
    val data: LiveData<DataOrException<UserModel, Boolean, Exception>> get() = _data

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
}