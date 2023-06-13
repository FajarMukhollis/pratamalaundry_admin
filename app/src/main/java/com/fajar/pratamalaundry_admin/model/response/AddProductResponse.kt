package com.fajar.pratamalaundry_admin.model.response

import com.google.gson.annotations.SerializedName

data class AddProductResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String
)