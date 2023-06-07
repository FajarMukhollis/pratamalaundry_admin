package com.fajar.pratamalaundry_admin.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fajar.pratamalaundry_admin.model.preference.AdminPreference

class SplashViewModel(
    private val pref: AdminPreference
): ViewModel() {

    fun getIsLogin() = pref.getIsLogin().asLiveData()
}