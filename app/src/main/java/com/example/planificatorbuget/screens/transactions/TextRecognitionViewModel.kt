package com.example.planificatorbuget.screens.transactions

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planificatorbuget.repository.TextRecognitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextRecognitionViewModel @Inject constructor(
    private val repository: TextRecognitionRepository
) : ViewModel() {

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    fun recognizeText(uri: Uri) {
        viewModelScope.launch {
            val result = repository.recognizeTextFromImage(uri)
            result.onSuccess { text ->
                _recognizedText.value = text
            }.onFailure { error ->
                _recognizedText.value = "Error: ${error.message}"
            }
        }
    }

    fun resetRecognizedText() {
        _recognizedText.value = ""
    }
}