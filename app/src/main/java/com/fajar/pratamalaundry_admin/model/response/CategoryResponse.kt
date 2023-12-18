package com.fajar.pratamalaundry_admin.model.response

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,
)