package com.example.planificatorbuget.screens.recurringtransactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.model.RecurringTransactionModel
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.repository.CategoryRepository
import com.example.planificatorbuget.repository.RecurringTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
}