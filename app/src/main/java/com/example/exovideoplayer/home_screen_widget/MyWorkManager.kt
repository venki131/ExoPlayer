package com.example.exovideoplayer.home_screen_widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.exovideoplayer.domain.repository.SamplePagingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@RequiresApi(Build.VERSION_CODES.O)
@HiltWorker
class MyWorkManager @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val repository: SamplePagingRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("WorkManager", "do work called in workManager")

            val result = repository.getPosts()

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetIds =
                appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            for (widgetId in widgetIds) {
                updateAppWidget(
                    context,
                    appWidgetId = widgetId,
                    appWidgetManager = appWidgetManager,
                    widgetText = result[generateRandomNumber()].title
                )
            }
            Log.d("WorkManager", result[generateRandomNumber()].title)
            Result.success()
        } catch (e: Exception) {
            // Handle errors
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "MyPeriodicWorker"
    }
}