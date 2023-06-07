package com.fajar.pratamalaundry_admin.model.usecase

import androidx.lifecycle.LiveData
import com.fajar.pratamalaundry_admin.model.repository.IAdminRepository
import com.fajar.pratamalaundry_admin.model.response.LoginResponse
import com.fajar.pratamalaundry_admin.model.result.Result

class AdminInteract (private val userRepository: IAdminRepository):
    AdminUseCase {
    override fun loginAdmin(username: String, pass: String): LiveData<Result<LoginResponse>> =
        userRepository.loginAdmin(username, pass)
}