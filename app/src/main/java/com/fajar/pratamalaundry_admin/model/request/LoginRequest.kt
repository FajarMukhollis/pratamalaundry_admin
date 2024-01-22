package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest (

    @SerializedName("username")
    var username : String,

    @SerializedName("password")
    var password : String,

    @SerializedName("fcm_petugas")
    var fcm_petugas : String
        )