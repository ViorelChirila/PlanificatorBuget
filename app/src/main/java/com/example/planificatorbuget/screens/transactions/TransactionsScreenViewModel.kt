package com.example.planificatorbuget.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsScreenViewModel @Inject constructor(private val repository: TransactionRepository): ViewModel(){


    fun addTransaction(transaction: TransactionModel){
        viewModelScope.launch {
            repository.addTransaction(transaction)
        }
    }
}