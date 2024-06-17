package com.example.planificatorbuget.screens.csvuploadscreen

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.utils.validateCsvContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class CsvViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _transactions = MutableLiveData<List<TransactionModel>>()
    val transactions: LiveData<List<TransactionModel>> get() = _transactions

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun readCsvFile(uri: Uri) {
        Log.d("CsvViewModel", "readCsvFile called with uri: $uri")
        viewModelScope.launch {
            val content = readCsvFileContent(uri)
            if (content != null) {
                try {
                    val (isValid, validationResult) = validateCsvContent(content)
                    if (isValid) {
                        _transactions.value = validationResult
                        _errorMessage.value = null
                    } else {
                        _errorMessage.value = "Fisierul CSV nu are un format valid"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error parsing CSV: ${e.message}"
                }
            } else {
                _errorMessage.value = "Errorare la citirea fisierului CSV"
            }
        }
    }

    private suspend fun readCsvFileContent(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = getApplication<Application>().contentResolver.openInputStream(uri)
                inputStream?.bufferedReader().use { it?.readText() }
            } catch (e: Exception) {
                Log.e("CsvViewModel", "Error reading file: ${e.message}")
                null
            }
        }
    }

    fun resetTransactions() {
        _transactions.value = emptyList()
    }

}