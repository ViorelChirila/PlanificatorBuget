package com.example.planificatorbuget.screens.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesScreenViewModel @Inject constructor(private val categoriesRepository: CategoryRepository): ViewModel(){
    private val _categories = MutableStateFlow(DataOrException<List<TransactionCategoriesModel>, Boolean, Exception>())
    val categories: StateFlow<DataOrException<List<TransactionCategoriesModel>, Boolean, Exception>> get() = _categories

    private val _categoryAddResult = MutableLiveData<Response<Boolean>>()
    val categoryAddResult: LiveData<Response<Boolean>> get() = _categoryAddResult
    init {
        fetchCategoriesFromFirebase()
    }
    private fun fetchCategoriesFromFirebase() {
        viewModelScope.launch {
            val result = categoriesRepository.fetchCategories()
            _categories.value = result
            Log.d("CategoriesScreenViewModel", "fetchCategories: ${_categories.value}")
        }
    }

    fun addCategory(category: TransactionCategoriesModel){
        viewModelScope.launch {
            _categoryAddResult.value = Response.Loading()
            val result = categoriesRepository.addCategory(category)
            _categoryAddResult.postValue(result)
            Log.d("CategoriesScreenViewModel", "addTransaction: ${categoryAddResult.value.toString()}")
            if (result is Response.Success){
                delay(1000L)
                _categoryAddResult.postValue(Response.Success(false))
                Log.d("CategoriesScreenViewModel", "addTransaction: ${categoryAddResult.value.toString()}")
            }
        }
    }
}