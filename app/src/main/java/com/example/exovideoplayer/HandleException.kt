package com.example.exovideoplayer

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HandleException(
    private val context: Context,
    private val getActivity: Activity
) {
    private val errorMessages = mutableListOf<String>()

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        errorMessages.add(throwable.message ?: "Unknown error message")
    }

    private val scope = CoroutineScope(Dispatchers.Default + exceptionHandler)

    fun dummyApiCall() {
        // Example coroutines making API calls in parallel
        val apiCallResults = List(NUMBER_OF_API_CALLS) {
            scope.async {
                try {
                    // Simulate a failed API call
                    throw RuntimeException("Error in API call $it")
                } catch (e: Exception) {
                    // Handle the exception, if needed
                    // Note: Don't rethrow the exception here; let the exceptionHandler handle it
                    Log.d("HandleException", e.message ?: "Custom Error Message")
                    e.message
                }
            }
        }

        // When all coroutines have finished executing
        scope.launch {
            val errorMessages = apiCallResults.awaitAll().firstOrNull { it != null }

            Log.d("HandleException", "Final Error Message = $errorMessages")

            // Show a single toast message with aggregated error messages
            errorMessages?.let { showToast(it) }
        }
    }

    private fun showToast(message: String) {
        getActivity.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val NUMBER_OF_API_CALLS = 5
    }
}