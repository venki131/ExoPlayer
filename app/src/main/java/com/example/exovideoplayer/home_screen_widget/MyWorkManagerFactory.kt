package com.example.exovideoplayer.home_screen_widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.exovideoplayer.domain.repository.SamplePagingRepository
import dagger.assisted.AssistedInject

class MyWorkManagerFactory @AssistedInject constructor(
    private val repository: SamplePagingRepository
) : ChildWorkerFactory {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
        return MyWorkManager(appContext, params, repository)
    }

}
