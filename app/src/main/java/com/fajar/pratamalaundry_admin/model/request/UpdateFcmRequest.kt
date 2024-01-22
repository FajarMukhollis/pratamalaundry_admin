package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class UpdateFcmRequest(
    @SerializedName("id_petugas")
    val id_petugas : String,

    @SerializedName("fcm_petugas")
    val fcm_petugas : String
)