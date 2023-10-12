package com.example.exovideoplayer

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters


@RequiresApi(Build.VERSION_CODES.O)
class MyWorkManager(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        return try {
            Log.d("WorkManager", "do work called in workManager")

            updateWidgetText(context, "Sample Text")

            Result.success()
        } catch (e: Exception) {
            // Handle errors
            Result.failure()
        }
    }

    private fun updateWidgetText(context: Context, newText: String?) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))

        val views = RemoteViews(context.packageName, R.layout.new_app_widget)
        views.setTextViewText(R.id.appwidget_text, newText)

        for (widgetId in widgetIds) {
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}