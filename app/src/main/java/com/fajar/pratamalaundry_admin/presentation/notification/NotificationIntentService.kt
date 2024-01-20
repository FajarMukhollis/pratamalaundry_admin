package com.fajar.pratamalaundry_admin.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.presentation.transaction.TransactionActivity

private const val CHANNEL_ID = "Pratama_Laundry_Channel"
private const val CHANNEL_NAME = "Pratama Laundry"
private const val NOTIFICATION_ID = 1

class NotificationIntentService : JobIntentService() {

    companion object {
        private const val JOB_ID = 1000

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, NotificationIntentService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent, ) {
        val title = "Pesanan Baru"
        val content = "Ada pesanan baru yang harus anda konfirmasi."
        showNotification(title, content, )
    }

    private fun showNotification(title: String, content: String) {
        val notificationIntent = Intent(this, TransactionActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
