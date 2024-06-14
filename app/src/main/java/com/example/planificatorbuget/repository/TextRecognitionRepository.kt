package com.example.planificatorbuget.repository

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TextRecognitionRepository@Inject constructor(private val context: Context) {
    suspend fun recognizeTextFromImage(uri: Uri): Result<String> {
        return try {
            val inputImage = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val visionText = recognizer.process(inputImage).await()
            Result.success(visionText.text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}