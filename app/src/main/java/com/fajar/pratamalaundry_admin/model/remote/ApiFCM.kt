package com.fajar.pratamalaundry_admin.model.remote

import com.fajar.pratamalaundry_admin.model.request.NotificationRequest
import com.fajar.pratamalaundry_admin.model.response.NotificationResponse
import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiFCM {

    @POST("fcm/send")
    fun sendNotification(
        @Header("Content-Type") contentType: String,
        @Header("Authorization") authKey: String,
        @Body notification: NotificationRequest
    ): Call<NotificationResponse>
}