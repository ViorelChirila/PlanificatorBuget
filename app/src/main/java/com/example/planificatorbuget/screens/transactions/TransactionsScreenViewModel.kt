package com.example.planificatorbuget.screens.transactions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.RecurringTransactionModel
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.repository.RecurringTransactionRepository
import com.example.planificatorbuget.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsScreenViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository
) : ViewModel() {

    private val _transactionAddResult = MutableLiveData<Response<Boolean>>()
    val transactionAddResult: LiveData<Response<Boolean>> get() = _transactionAddResult

    private val _recurringTransactionAddResult = MutableLiveData<Response<Boolean>>()
    val recurringTransactionAddResult: LiveData<Response<Boolean>> get() = _recurringTransactionAddResult

    private val _transactions =
        MutableStateFlow(DataOrException<List<TransactionModel>, Boolean, Exception>())
    val transactions: StateFlow<DataOrException<List<TransactionModel>, Boolean, Exception>> get() = _transactions

    init {
        fetchTransactionsFromDatabase()
    }

    fun fetchTransactionsFromDatabase() {
        viewModelScope.launch {
            _transactions.value.isLoading = true
            val result = repository.fetchTransactions()
            _transactions.value = result
            Log.d("TransactionsScreenViewModel", "fetchTransactions: ${_transactions.value}")
        }
    }

    fun addTransaction(transaction: TransactionModel) {
        viewModelScope.launch {
            _transactionAddResult.value = Response.Loading()
            val result = repository.addTransaction(transaction)
            _transactionAddResult.postValue(result)
            Log.d(
                "TransactionsScreenViewModel",
                "addTransaction: ${transactionAddResult.value.toString()}"
            )
            if (result is Response.Success) {
                delay(1000L)
                _transactionAddResult.postValue(Response.Success(false))
                Log.d(
                    "TransactionsScreenViewModel",
                    "addTransaction: ${transactionAddResult.value.toString()}"
                )
            }
        }
    }

    fun addRecurringTransaction(recurringTransaction: RecurringTransactionModel) {
        viewModelScope.launch {
            _recurringTransactionAddResult.value = Response.Loading()
            val result = recurringTransactionRepository.addRecurringTransaction(recurringTransaction)
            _recurringTransactionAddResult.postValue(result)
            Log.d(
                "TransactionsScreenViewModel",
                "addRecurringTransaction: ${recurringTransactionAddResult.value.toString()}"
            )
            if (result is Response.Success) {
                delay(1000L)
                _recurringTransactionAddResult.postValue(Response.Success(false))
                Log.d(
                    "TransactionsScreenViewModel",
                    "addRecurringTransaction: ${recurringTransactionAddResult.value.toString()}"
                )
            }
        }
    }
}