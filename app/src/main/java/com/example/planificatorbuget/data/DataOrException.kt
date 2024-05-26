package com.example.planificatorbuget.data

data class DataOrException<T,Boolean,Exception>(
    var data: T? = null,
    var isLoading: Boolean? = null,
    var exception: Exception? = null
)