package com.fajar.pratamalaundry_admin.presentation.notification

import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.*

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title")
        val body = inputData.getString("body")

        // Kirim notifikasi di sini

        return Result.success()
    }

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            val workManager = WorkManager.getInstance(context)
            val dataBuilder = Data.Builder()

            // Menggunakan putString untuk setiap elemen di intent
            for (key in intent.extras!!.keySet()) {
                dataBuilder.putString(key, intent.extras!!.getString(key))
            }

            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(dataBuilder.build())
                .build()

            workManager.enqueue(workRequest)
        }
    }
}
