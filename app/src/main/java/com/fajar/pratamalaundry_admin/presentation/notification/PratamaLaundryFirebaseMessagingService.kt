package com.fajar.pratamalaundry_admin.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.UpdateFcmRequest
import com.fajar.pratamalaundry_admin.model.response.UpdateFcmResponse
import com.fajar.pratamalaundry_admin.presentation.main.MainActivity
import com.fajar.pratamalaundry_admin.presentation.main.MainViewModel
import com.fajar.pratamalaundry_admin.presentation.transaction.TransactionActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PratamaLaundryFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var mainViewModel: MainViewModel

    companion object {
        private const val TAG = "PratamaLaundryFirebaseMessagingService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.isNotEmpty().let {
            if (it) {
                val title = remoteMessage.data["title"]
                val body = remoteMessage.data["body"]
                val payloadDataStatusBarang = remoteMessage.data

                if (payloadDataStatusBarang.isNotEmpty()) {
                    if (payloadDataStatusBarang["status_barang"] == "Menunggu Konfirmasi" && payloadDataStatusBarang["type"] == "new_order" ){
                        sendNotificationStatusBarang(title, body)
                    } else {
                        Log.d(TAG, "Message data payloadDataStatusBarang: $payloadDataStatusBarang")
                    }
                } else {
                    Log.d(TAG, "Message data empty payloadDataStatusBarang: $payloadDataStatusBarang")
                }
            }
        }

        remoteMessage.data.isNotEmpty().let {
            if (it) {
                val title = remoteMessage.data["title"]
                val body = remoteMessage.data["body"]
                val payloadComplaint = remoteMessage.data

                if(payloadComplaint.isNotEmpty()){
                    if(payloadComplaint["complaint"] == "Komplain Baru" && payloadComplaint["type"] == "new_complaint"){
                        sendNotificationComplain(title, body)
                    } else {
                        Log.d(TAG, "Message data empty payloadComplaint: $payloadComplaint")
                    }
                } else {
                    Log.d(TAG, "Message data empty payloadComplaint: $payloadComplaint")
                }
            }
        }

        remoteMessage.notification?.let {
            sendNotificationStatusBarang(it.title ?: "", it.body ?: "")
            sendNotificationComplain(it.title ?: "", it.body ?: "")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Update token FCM ke server
        sendNewToken(token)
    }

    private fun sendNewToken(token: String) {
        val retroInstance = ApiConfig.getApiService()
        GlobalScope.launch {
            val idPetugas = mainViewModel.getNama().value?.localId
            val newTokenFCM = mainViewModel.getTokenFcm()
            val req = UpdateFcmRequest(idPetugas.toString(), newTokenFCM)
            val call = retroInstance.updateFcm(req)
            call.enqueue(object : Callback<UpdateFcmResponse> {
                override fun onResponse(
                    call: Call<UpdateFcmResponse>,
                    response: Response<UpdateFcmResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("FCM", "Token FCM berhasil diupdate")
                    } else {
                        Log.d("FCM", "Token FCM gagal diupdate")
                    }
                }

                override fun onFailure(call: Call<UpdateFcmResponse>, t: Throwable) {
                    Log.e("FCM", "Gagal mengirim token FCM", t)
                }
            })
        }
    }

    private fun sendNotificationStatusBarang(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.action = "com.fajar.pratamalaundry_admin.NOTIFICATION_CLICK"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "default"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pratama Laundry",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendNotificationComplain(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.action = "com.fajar.pratamalaundry_admin.NOTIFICATION_COMPLAINT"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "complaint"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,  
                "PratamaLaundry",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

}