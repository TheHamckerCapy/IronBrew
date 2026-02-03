package com.example.beerbicep

sealed class Resource_Class<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource_Class<T>(data)
    class Error<T>(message: String?) : Resource_Class<T>(message = message)
    class Loading<T>(val isLoading: Boolean = true) : Resource_Class<T>(null)
}