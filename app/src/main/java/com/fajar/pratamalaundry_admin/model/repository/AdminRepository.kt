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
    override fun loginAdmin(username: String, pass: String, fcm: String): LiveData<Result<LoginResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = apiService.loginAdmin(LoginRequest(username, pass, fcm))

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true && responseBody.data != null) {
                        emit(Result.Success(responseBody))
                    } else {
                        emit(Result.Error("Username dan Password Tidak diketahui"))
                    }
                } else {
                    emit(Result.Error("Gagal melakukan permintaan login"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
}