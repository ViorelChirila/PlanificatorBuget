package com.example.planificatorbuget

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.planificatorbuget.repository.RecurringTransactionRepository
import com.example.planificatorbuget.repository.TransactionRepository
import com.example.planificatorbuget.repository.UserRepository
import com.example.planificatorbuget.workers.RecurringTransactionWorker
import com.example.planificatorbuget.workers.scheduleRecurringTransactions
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PlannerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: RecurringTransactionWorkerFactory
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannel() {
        val name = "Transaction Notification Channel"
        val descriptionText = "Channel for transaction notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("transaction_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

class RecurringTransactionWorkerFactory @Inject constructor(
    private val repository: RecurringTransactionRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = RecurringTransactionWorker(
        appContext,
        workerParameters,
        repository,
        transactionRepository,
        userRepository
    )

}