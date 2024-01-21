package com.fajar.pratamalaundry_admin.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fajar.pratamalaundry_admin.model.preference.AdminPreference

class MainViewModel(
    private val adminPreference: AdminPreference
):ViewModel() {

    fun getNama() = adminPreference.getAdmin().asLiveData()

    suspend fun saveTokenFcm(token: String) {
        adminPreference.saveTokenFcm(token)
    }
    suspend fun getTokenFcm() : String {
        return adminPreference.getTokenFcm()
    }

}