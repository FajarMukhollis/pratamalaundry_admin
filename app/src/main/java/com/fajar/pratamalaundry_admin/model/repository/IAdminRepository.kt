package com.fajar.pratamalaundry_admin.model.repository

import androidx.lifecycle.LiveData
import com.fajar.pratamalaundry_admin.model.response.LoginResponse
import com.fajar.pratamalaundry_admin.model.result.Result

interface IAdminRepository {

    fun loginAdmin(username: String, pass: String, fcm: String): LiveData<Result<LoginResponse>>
}