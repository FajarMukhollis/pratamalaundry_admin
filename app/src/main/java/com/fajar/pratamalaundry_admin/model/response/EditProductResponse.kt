package com.fajar.pratamalaundry_admin.model.response

import com.google.gson.annotations.SerializedName

data class EditProductResponse(

    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String
)