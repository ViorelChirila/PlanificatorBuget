package com.example.planificatorbuget.screens.csvuploadscreen

import android.util.Log
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
import com.example.planificatorbuget.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CsvUploadFileScreenViewModel @Inject constructor(
    private val transactionsRepository: TransactionRepository,
    private val categoriesRepository: CategoryRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _isProcessing = MutableLiveData<Boolean>()
    val isProcessing: LiveData<Boolean> get() = _isProcessing

    private val _transactionAdditionStatus = MutableLiveData<Boolean?>()
    val transactionAdditionStatus: LiveData<Boolean?> get() = _transactionAdditionStatus

    private val _categories = MutableStateFlow(DataOrException<List<TransactionCategoriesModel>, Boolean, Exception>())
    val categories: StateFlow<DataOrException<List<TransactionCategoriesModel>, Boolean, Exception>> get() = _categories

    init {
        fetchCategoriesFromFirebase()
    }
    fun resetTransactionAdditionStatus() {
        _transactionAdditionStatus.value = null
    }
    fun fetchCategoriesFromFirebase() {
        viewModelScope.launch {
            val result = categoriesRepository.fetchCategories()
            _categories.value = result
            Log.d("CategoriesScreenViewModel", "fetchCategories: ${_categories.value}")
        }
    }
    fun addNewTransactions(newTransactions: List<TransactionModel>) {
        viewModelScope.launch {
            var allAddedSuccessfully = true
            _isProcessing.value = true
            val existingTransactions = transactionsRepository.fetchTransactions().data ?: emptyList()
            newTransactions.forEach { newTransaction  ->
                val isDuplicate = existingTransactions.any { existingTransaction ->
                    existingTransaction.transactionTitle == newTransaction.transactionTitle &&
                            existingTransaction.amount == newTransaction.amount &&
                            existingTransaction.transactionType == newTransaction.transactionType &&
                            existingTransaction.transactionDate == newTransaction.transactionDate &&
                            existingTransaction.transactionDescription == newTransaction.transactionDescription
                }
                if (!isDuplicate) {
                    try {
                        userRepository.fetchUser().let { user ->
                            newTransaction.budgetSnapshot = user.data?.currentBudget ?: 0.0
                            val budget = user.data?.currentBudget?.plus(newTransaction.amount)
                            val formattedBudget = String.format("%.2f", budget)
                            if (budget != null) {
                                userRepository.updateUserCurrentBudget(formattedBudget.toDouble())
                            }
                        }

                        transactionsRepository.addTransaction(newTransaction)
                    } catch (e: Exception) {
                        allAddedSuccessfully = false
                        Log.d("CsvViewModel", "Erroare la adaugarea tranzactiilor: ${e.message}")
                    }

                }
            }
            _transactionAdditionStatus.value = allAddedSuccessfully
            _isProcessing.value = false

        }
    }
}