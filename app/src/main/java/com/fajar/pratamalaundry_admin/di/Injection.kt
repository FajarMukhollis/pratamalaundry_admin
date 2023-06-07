package com.fajar.pratamalaundry_admin.di

import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.repository.AdminRepository
import com.fajar.pratamalaundry_admin.model.repository.IAdminRepository
import com.fajar.pratamalaundry_admin.model.usecase.AdminInteract
import com.fajar.pratamalaundry_admin.model.usecase.AdminUseCase

object Injection {

    private fun provideUserRepository(): IAdminRepository {
        val apiConfig = ApiConfig.getApiService()
        return AdminRepository(apiConfig)
    }

    fun provideUserUseCase(): AdminUseCase {
        val repository = provideUserRepository()
        return AdminInteract(repository)
    }
}