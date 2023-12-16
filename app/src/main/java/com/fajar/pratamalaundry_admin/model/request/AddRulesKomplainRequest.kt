package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class AddRulesKomplainRequest (
    @SerializedName("aturan")
    val aturan: String
)