package com.fajar.pratamalaundry_admin.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fajar.pratamalaundry_admin.model.admin.AdminModel
import com.fajar.pratamalaundry_admin.model.preference.AdminPreference
import com.fajar.pratamalaundry_admin.model.response.LoginResponse
import com.fajar.pratamalaundry_admin.model.usecase.AdminUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val adminUseCase: AdminUseCase,
    private val adminPreference: AdminPreference
):ViewModel() {

    fun loginAdmin(username: String, pass: String) = adminUseCase.loginAdmin(username, pass)

    fun saveUser(adminLogin: LoginResponse) {
        viewModelScope.launch {
            val data = AdminModel(
                adminLogin.data.id_petugas,
                adminLogin.data.username,
                adminLogin.data.nama_petugas,
                adminLogin.token,
                true
            )
            adminPreference.saveUser(data)
        }
    }

    fun getToken() = adminPreference.getToken().asLiveData()
}