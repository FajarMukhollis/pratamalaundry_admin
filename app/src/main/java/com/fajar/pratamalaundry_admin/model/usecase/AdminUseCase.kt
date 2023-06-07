package com.fajar.pratamalaundry_admin.model.usecase

import androidx.lifecycle.LiveData
import com.fajar.pratamalaundry_admin.model.response.LoginResponse
import com.fajar.pratamalaundry_admin.model.result.Result

interface AdminUseCase {
    fun loginAdmin(username: String, pass: String): LiveData<Result<LoginResponse>>
}