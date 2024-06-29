package com.example.planificatorbuget.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.planificatorbuget.R
import com.example.planificatorbuget.model.RecurringTransactionModel
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.repository.RecurringTransactionRepository
import com.example.planificatorbuget.repository.TransactionRepository
import com.example.planificatorbuget.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.util.Date
import java.util.concurrent.TimeUnit

@HiltWorker
class RecurringTransactionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val repository: RecurringTransactionRepository,
    @Assisted private val transactionRepository: TransactionRepository,
    @Assisted private val userRepository: UserRepository
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {

        delay(10000L) // Wait for 10 seconds

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.d("RecurringTransactionWorker", "No user")
            return Result.retry()
        }

        Log.d("RecurringTransactionWorker", currentUser.uid)

        val recurringTransactions = repository.fetchRecurringTransactions()
        for (recurringTransaction in recurringTransactions) {
            handleRecurringTransaction(recurringTransaction)
            Log.d("RecurringTransactionWorker", recurringTransaction.transactionTitle)
        }

        return Result.success()
    }

    private suspend fun handleRecurringTransaction(
        recurringTransaction: RecurringTransactionModel,
    ) {
        val currentDate = Timestamp.now().toDate()
        val startDate = recurringTransaction.startDate.toDate()
        val endDate = recurringTransaction.endDate.toDate()
        val userData = userRepository.fetchUser()

        if (currentDate.after(endDate)) {
            repository.updateRecurrentTransactionStatus(recurringTransaction.transactionId!!, "inactiv")
            return
        }

        val shouldCreateTransaction = when (recurringTransaction.recurrenceInterval) {
            "Zilnic" -> isDateDue(startDate, currentDate, 1, TimeUnit.DAYS)
            "Saptamanal" -> isDateDue(startDate, currentDate, 7, TimeUnit.DAYS)
            "Lunar" -> isDateDue(startDate, currentDate, 30, TimeUnit.DAYS)
            else -> false
        }

        if (shouldCreateTransaction) {
            Log.d("RecurringTransactionWorker", "Creating transaction")
            val transaction = TransactionModel(
                userId = recurringTransaction.userId,
                amount = recurringTransaction.amount,
                transactionType = recurringTransaction.transactionType,
                categoryId = recurringTransaction.categoryId,
                transactionDate = Timestamp.now(),
                transactionTitle = recurringTransaction.transactionTitle,
                transactionDescription = recurringTransaction.transactionDescription,
                budgetSnapshot = userData.data?.currentBudget ?: 0.0,
                descriptionImage = recurringTransaction.descriptionImageUri
            )

            transactionRepository.addTransaction(transaction)
            Log.d("RecurringTransactionWorker", "Transaction created")
            userRepository.updateUserCurrentBudget(
                userData.data?.currentBudget?.plus(
                    recurringTransaction.amount
                ) ?: 0.0
            )
            sendNotification(transaction.transactionTitle, transaction.amount)
        }
    }
    private fun sendNotification(transactionTitle: String, amount: Double) {
        val builder = NotificationCompat.Builder(applicationContext, "transaction_channel")
            .setSmallIcon(R.drawable.logo) // Adjust the icon according to your project
            .setContentTitle("O nouă tranzacție a fost adaugată")
            .setContentText("Tranzactie: $transactionTitle cu valoarea $amount lei a fost adaugată.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(1, builder.build())
        }
    }

    private fun isDateDue(
        startDate: Date,
        currentDate: Date,
        interval: Long,
        timeUnit: TimeUnit
    ): Boolean {
        val diff = currentDate.time - startDate.time
        val daysDiff = TimeUnit.MILLISECONDS.toDays(diff)
        Log.d("RecurringTransactionWorker", "Days diff: $daysDiff")
        return daysDiff % timeUnit.toDays(interval) == 0L
    }
}

