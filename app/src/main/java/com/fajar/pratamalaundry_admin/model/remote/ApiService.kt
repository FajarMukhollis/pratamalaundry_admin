package com.fajar.pratamalaundry_admin.model.remote

import com.fajar.pratamalaundry_admin.model.request.AddProductRequest
import com.fajar.pratamalaundry_admin.model.request.DeleteProductRequest
import com.fajar.pratamalaundry_admin.model.request.LoginRequest
import com.fajar.pratamalaundry_admin.model.response.AddProductResponse
import com.fajar.pratamalaundry_admin.model.response.DeleteProductResponse
import com.fajar.pratamalaundry_admin.model.response.LoginResponse
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import retrofit2.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.*

interface ApiService {

    @POST("adminlogin")
    suspend fun loginAdmin(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("get_product")
    fun getProduct(): Call<ProductResponse>

    @POST("add_product")
    fun addProduct(
        @Body addProductRequest: AddProductRequest
    ): Call<AddProductResponse>

    @HTTP(method = "DELETE", path = "delete_product", hasBody = true)
    fun deleteProduct(
        @Body deleteProductRequest : DeleteProductRequest
    ): Call<DeleteProductResponse>
}