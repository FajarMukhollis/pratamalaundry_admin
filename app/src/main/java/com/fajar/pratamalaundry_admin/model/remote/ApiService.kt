package com.fajar.pratamalaundry_admin.model.remote

import com.fajar.pratamalaundry_admin.model.request.LoginRequest
import com.fajar.pratamalaundry_admin.model.response.LoginResponse
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import retrofit2.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.*

interface ApiService {

    @POST("admin/login")
    suspend fun loginAdmin(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("product/product")
    fun getProduct(): Call<ProductResponse>
}