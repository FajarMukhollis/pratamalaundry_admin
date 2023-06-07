package com.fajar.pratamalaundry_admin.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: String,

    @SerializedName("data")
    val `data`: Data
)

data class Data(
    @SerializedName("id_petugas")
    val id_petugas: Int,

    @SerializedName("nama_petugas")
    val nama_petugas: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String

)