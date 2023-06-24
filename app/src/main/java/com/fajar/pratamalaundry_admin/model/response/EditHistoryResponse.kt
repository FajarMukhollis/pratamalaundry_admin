package com.fajar.pratamalaundry_admin.model.response

import com.google.gson.annotations.SerializedName

data class EditHistoryResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Boolean
)