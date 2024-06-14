package com.example.planificatorbuget.screens.transactiondetailsscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.repository.CategoryRepository
import com.example.planificatorbuget.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsScreenViewModel @Inject constructor(
    private val transactionsRepository: TransactionRepository,
    private val categoriesRepository: CategoryRepository
) : ViewModel() {

    private val _transaction = MutableStateFlow(DataOrException<TransactionModel, Boolean, Exception>())
    val transaction: StateFlow<DataOrException<TransactionModel, Boolean, Exception>> get() = _transaction
    fun fetchTransactionById(transactionId:String){
        viewModelScope.launch {
            _transaction.value.isLoading = true
            val result = transactionsRepository.fetchTransactionById(transactionId)
            Log.d("TransactionDetailsScreenViewModel", "fetchTransactionById: ${_transaction.value}")
            _transaction.value = result
        }
    }
}