package com.example.planificatorbuget.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
): ViewModel() {
    private val _transactions =
        MutableStateFlow(DataOrException<List<TransactionModel>, Boolean, Exception>())
    val transactions: StateFlow<DataOrException<List<TransactionModel>, Boolean, Exception>> get() = _transactions

    init {
        fetchTransactionsFromDatabase()
    }

    fun fetchTransactionsFromDatabase() {
        viewModelScope.launch {
            _transactions.value.isLoading = true
            val result = transactionRepository.fetchTransactions()
            _transactions.value = result
            Log.d("TransactionsScreenViewModel", "fetchTransactions: ${_transactions.value}")
        }
    }
}