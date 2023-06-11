package com.fajar.pratamalaundry_admin.model.admin

data class AdminModel(
    val localId: Int,
    val username:String,
    val nama_petugas:String,
    val token:String,
    val isLogin:Boolean
)