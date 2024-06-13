package com.example.planificatorbuget.screens.recurringtransactions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.RecurringTransactionModel
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.repository.CategoryRepository
import com.example.planificatorbuget.repository.RecurringTransactionRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecurringTransactionsScreenViewModel @Inject constructor(
    private val repository: RecurringTransactionRepository,
    private val categoryRepository: CategoryRepository
) :
    ViewModel() {

    private val _recurringTransactions = MutableStateFlow(listOf<RecurringTransactionModel>())
    val recurringTransactions: StateFlow<List<RecurringTransactionModel>> get() = _recurringTransactions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _categories = MutableStateFlow(mapOf<String,TransactionCategoriesModel>())
    val categories: StateFlow<Map<String,TransactionCategoriesModel>> get() = _categories

    private val _recurringTransactionUpdateResult = MutableLiveData<Response<Boolean>>()
    val recurringTransactionUpdateResult: LiveData<Response<Boolean>> get() = _recurringTransactionUpdateResult

    init {
        fetchRecurringTransactionsFromDatabase()
    }

    fun fetchRecurringTransactionsFromDatabase() {
        viewModelScope.launch {
            _isLoading.value = true
            _recurringTransactions.value = emptyList()
            repository.fetchRecurringTransactions().let {
                _recurringTransactions.value = it
                for (recurringTransaction in it) {
                    categoryRepository.fetchCategoryById(recurringTransaction.categoryId.toString()).let { category ->
                        _categories.value += (recurringTransaction.transactionId!! to category!!)
                    }
                }
            }
            _isLoading.value = false
        }
    }

    fun updateRecurringTransaction(recurringTransactionId:String, startDate:Timestamp, endDate:Timestamp, recurrenceInterval:String) {
        viewModelScope.launch {
            _recurringTransactionUpdateResult.value = Response.Loading()
            val result = repository.updateRecurringTransaction(recurringTransactionId, startDate, endDate, recurrenceInterval)
            _recurringTransactionUpdateResult.postValue(result)
            if (result is Response.Success) {
                delay(1000L)
                _recurringTransactionUpdateResult.postValue(Response.Success(false))
            }
        }
    }

    fun deleteRecurringTransaction(transactionId: String) {
        viewModelScope.launch {

            val result = repository.deleteRecurringTransaction(transactionId)
            if (result is Response.Success) {
                fetchRecurringTransactionsFromDatabase()
            }

        }
    }

    fun updateRecurrentTransactionStatus(transactionId: String, status: String) {
        viewModelScope.launch {
            _recurringTransactionUpdateResult.value = Response.Loading()
            val result = repository.updateRecurrentTransactionStatus(transactionId, status)
            _recurringTransactionUpdateResult.postValue(result)
            if (result is Response.Success) {
                delay(1000L)
                _recurringTransactionUpdateResult.postValue(Response.Success(false))
            }
        }
    }
}