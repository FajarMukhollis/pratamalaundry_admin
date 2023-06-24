package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class DeleteHistoryRequest(
    @SerializedName("id_transaksi")
    val id_transaksi: String
)
