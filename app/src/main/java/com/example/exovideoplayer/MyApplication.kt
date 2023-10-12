package com.example.exovideoplayer

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.color.DynamicColors
import java.util.concurrent.TimeUnit

class MyApplication: Application() {

    private val TAG = MyApplication::class.java.simpleName
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        setupPeriodicRequest()
    }
    private fun setupPeriodicRequest() {
        val sharedPreferences = getSharedPreferences("YourPreferencesName", Context.MODE_PRIVATE)
        val isWorkScheduled = sharedPreferences.getBoolean(PREF_KEY_WORK_SCHEDULED, false)

        if (!isWorkScheduled) {
            // Set up periodic work using WorkManager
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // Network connectivity constraint
                .build()

            val repeatInterval = TIME_PERIOD // Repeat every 15 minutes
            val periodicWorkRequest = PeriodicWorkRequestBuilder<MyWorkManager>(
                repeatInterval, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(this).enqueue(periodicWorkRequest)

            // Mark the work as scheduled in shared preferences
            sharedPreferences.edit().putBoolean(PREF_KEY_WORK_SCHEDULED, true).apply()

            Log.d(TAG, "Periodic request scheduled")
        } else {
            Log.d(TAG, "Periodic request already scheduled")
        }
    }

    private companion object {
        const val TIME_PERIOD = 15L
        private const val PREF_KEY_WORK_SCHEDULED = "pref_key_work_scheduled"
    }
}