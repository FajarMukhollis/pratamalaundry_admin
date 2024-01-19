package com.fajar.pratamalaundry_admin.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fajar.pratamalaundry_admin.di.Injection
import com.fajar.pratamalaundry_admin.model.preference.AdminPreference
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.usecase.AdminUseCase
import com.fajar.pratamalaundry_admin.presentation.login.LoginViewModel
import com.fajar.pratamalaundry_admin.presentation.main.MainViewModel
import com.fajar.pratamalaundry_admin.presentation.main.NewTransaction
import com.fajar.pratamalaundry_admin.presentation.main.NewTransactionViewModel
import com.fajar.pratamalaundry_admin.presentation.profile.ProfileViewModel
import com.fajar.pratamalaundry_admin.presentation.splash.SplashViewModel

class ViewModelFactory(
    private val adminUseCase: AdminUseCase,
    private val pref: AdminPreference,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(
                pref
            ) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(
                adminUseCase, pref
            ) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                pref
            ) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(
                pref
            ) as T
            modelClass.isAssignableFrom(NewTransactionViewModel::class.java) -> NewTransactionViewModel(
                NewTransaction(ApiConfig.getApiService())
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }


    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context, pref: DataStore<Preferences>): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideUserUseCase(),
                    AdminPreference.getInstance(pref),
                )
            }.also { instance = it }
    }
}