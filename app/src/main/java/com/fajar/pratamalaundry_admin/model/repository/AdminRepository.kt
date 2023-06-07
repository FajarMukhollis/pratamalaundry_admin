package com.fajar.pratamalaundry_admin.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.fajar.pratamalaundry_admin.model.remote.ApiService
import com.fajar.pratamalaundry_admin.model.request.LoginRequest
import com.fajar.pratamalaundry_admin.model.response.LoginResponse
import com.fajar.pratamalaundry_admin.model.result.Result

class AdminRepository(
    private val apiService: ApiService
) : IAdminRepository {
    override fun loginAdmin(username: String, pass: String): LiveData<Result<LoginResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = apiService.loginAdmin(LoginRequest(username, pass))

                if (response.body()?.status == false) {
                    emit(Result.Error("salah di user repository"))
                } else if (response.body()?.status == true) {
                    emit(Result.Success(response.body()!!))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
}