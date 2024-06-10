package com.example.planificatorbuget.workers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
fun scheduleRecurringTransactions(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(
        repeatInterval = 1,
        repeatIntervalTimeUnit = TimeUnit.DAYS)

        .setBackoffCriteria(
            backoffPolicy = androidx.work.BackoffPolicy.LINEAR,
            duration = Duration.ofSeconds(15)
        )
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "RecurringTransactionWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}