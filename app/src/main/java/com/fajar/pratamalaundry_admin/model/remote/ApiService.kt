package com.fajar.pratamalaundry_admin.model.remote

import com.fajar.pratamalaundry_admin.model.request.AddProductRequest
import com.fajar.pratamalaundry_admin.model.request.DeleteHistoryRequest
import com.fajar.pratamalaundry_admin.model.request.DeleteProductRequest
import com.fajar.pratamalaundry_admin.model.request.EditHistoryRequest
import com.fajar.pratamalaundry_admin.model.request.EditProductRequest
import com.fajar.pratamalaundry_admin.model.request.LoginRequest
import com.fajar.pratamalaundry_admin.model.response.AddProductResponse
import com.fajar.pratamalaundry_admin.model.response.DeleteHistoryResponse
import com.fajar.pratamalaundry_admin.model.response.DeleteProductResponse
import com.fajar.pratamalaundry_admin.model.response.DetailTransaksiResponse
import com.fajar.pratamalaundry_admin.model.response.EditHistoryResponse
import com.fajar.pratamalaundry_admin.model.response.EditProductResponse
import com.fajar.pratamalaundry_admin.model.response.LoginResponse
import com.fajar.pratamalaundry_admin.model.response.OneMonthResponse
import com.fajar.pratamalaundry_admin.model.response.OneWeeksResponse
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import com.fajar.pratamalaundry_admin.model.response.TransactionResponse
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
        @Body deleteProductRequest: DeleteProductRequest
    ): Call<DeleteProductResponse>

    @PUT("edit_product")
    fun editProduct(
        @Body editProductRequest: EditProductRequest
    ): Call<EditProductResponse>

    @GET("get_history")
    fun getHistory(): Call<TransactionResponse>

    @HTTP(method = "DELETE", path = "delete_history", hasBody = true)
    fun deleteHistory(
        @Body deleteHistoryRequest: DeleteHistoryRequest
    ): Call<DeleteHistoryResponse>

    @PUT("edit_history")
    fun editHistory(
        @Body editHistoryRequest: EditHistoryRequest
    ): Call<EditHistoryResponse>

    @GET("get_oneWeeks")
    fun getOneWeeks(): Call<OneWeeksResponse>

    @GET("get_oneMonths")
    fun getOneMonths(): Call<OneMonthResponse>

    @GET("detail_transaksi/{id_transaksi}")
    fun getDetailTransaksi(
        @Path("id_transaksi") idTransaksi: String
    ): Call<DetailTransaksiResponse>
}