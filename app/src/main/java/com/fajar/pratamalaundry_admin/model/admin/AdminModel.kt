package com.fajar.pratamalaundry_admin.model.admin

data class AdminModel(
    val localId: Int,
    val username:String,
    val token:String,
    val isLogin:Boolean
)