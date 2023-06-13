package com.fajar.pratamalaundry_admin.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fajar.pratamalaundry_admin.model.admin.AdminModel
import com.fajar.pratamalaundry_admin.model.preference.AdminPreference
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreference: AdminPreference
): ViewModel() {

    fun getAdminData(): LiveData<AdminModel> = userPreference.getAdmin().asLiveData()

    fun signOut() {
        viewModelScope.launch {
            userPreference.logout()
        }
    }
}