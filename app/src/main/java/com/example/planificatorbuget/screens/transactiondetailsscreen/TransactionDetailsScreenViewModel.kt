package com.example.planificatorbuget.screens.transactiondetailsscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.repository.CategoryRepository
import com.example.planificatorbuget.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsScreenViewModel @Inject constructor(
    private val transactionsRepository: TransactionRepository,
    private val categoriesRepository: CategoryRepository
) : ViewModel() {

    private val _transaction = MutableStateFlow(DataOrException<TransactionModel, Boolean, Exception>(isLoading = true))
    val transaction: StateFlow<DataOrException<TransactionModel, Boolean, Exception>> get() = _transaction

    private val _categories = MutableStateFlow(DataOrException<TransactionCategoriesModel, Boolean, Exception>(isLoading = true))
    val categories: StateFlow<DataOrException<TransactionCategoriesModel, Boolean, Exception>> get() = _categories

    private val _transactionUpdateResult = MutableLiveData<Response<Boolean>>()
    val transactionUpdateResult: LiveData<Response<Boolean>> get() = _transactionUpdateResult

    fun fetchTransactionAndCategoryById(transactionId: String) {
        viewModelScope.launch {
            _transaction.value = DataOrException(isLoading = true)
            _categories.value = DataOrException(isLoading = true)

            try {
                val transactionResult = transactionsRepository.fetchTransactionById(transactionId)
                val transactionData = transactionResult.data

                if (transactionData != null) {
                    _transaction.value = DataOrException(data = transactionData, isLoading = false)

                    val categoryId = transactionData.categoryId
                    if (categoryId != null) {
                        val categoryResult = categoriesRepository.fetchCategoryById(categoryId)
                        _categories.value = DataOrException(data = categoryResult, isLoading = false)
                    } else {
                        _categories.value = DataOrException(data = null, isLoading = false, exception = Exception("Category ID is null"))
                    }
                } else {
                    _transaction.value = DataOrException(data = null, isLoading = false, exception = transactionResult.exception)
                    _categories.value = DataOrException(data = null, isLoading = false)
                }
            } catch (e: Exception) {
                _transaction.value = DataOrException(data = null, isLoading = false, exception = e)
                _categories.value = DataOrException(data = null, isLoading = false, exception = e)
            }
        }
    }

    fun updateTransactionDescription(transactionId: String, newDescription: String) {
        viewModelScope.launch {
            _transactionUpdateResult.value = Response.Loading()
            val result = transactionsRepository.updateTransactionDescription(transactionId, newDescription)
            _transactionUpdateResult.postValue(result)
            if (result is Response.Success) {
                delay(1000L)
                _transactionUpdateResult.postValue(Response.Success(false))
            }
        }
    }
}
