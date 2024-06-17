package com.example.planificatorbuget.screens.statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.repository.CategoryRepository
import com.example.planificatorbuget.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsScreenViewModel @Inject constructor(
    private val transactionsRepository: TransactionRepository,
    private val categoriesRepository: CategoryRepository
): ViewModel(){

    private val _transactions =
        MutableStateFlow(DataOrException<List<TransactionModel>, Boolean, Exception>())
    val transactions: StateFlow<DataOrException<List<TransactionModel>, Boolean, Exception>> get() = _transactions

    private val _categories = MutableStateFlow(DataOrException<List<TransactionCategoriesModel>, Boolean, Exception>())
    val categories: StateFlow<DataOrException<List<TransactionCategoriesModel>, Boolean, Exception>> get() = _categories

    init {
        fetchTransactionsFromDatabase()
        fetchCategoriesFromFirebase()
    }
    fun fetchTransactionsFromDatabase() {
        viewModelScope.launch {
            _transactions.value.isLoading = true
            val result = transactionsRepository.fetchTransactions()
            _transactions.value = result
            Log.d("TransactionsScreenViewModel", "fetchTransactions: ${_transactions.value}")
        }
    }

    fun fetchCategoriesFromFirebase() {
        viewModelScope.launch {
            val result = categoriesRepository.fetchCategories()
            _categories.value = result
            Log.d("CategoriesScreenViewModel", "fetchCategories: ${_categories.value}")
        }
    }

}