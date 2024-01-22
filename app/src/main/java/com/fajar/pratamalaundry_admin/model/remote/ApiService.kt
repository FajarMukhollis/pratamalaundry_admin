package com.fajar.pratamalaundry_admin.model.remote

import com.fajar.pratamalaundry_admin.model.request.*
import com.fajar.pratamalaundry_admin.model.response.*
import retrofit2.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.*

interface ApiService {

    @POST("admin/login")
    suspend fun loginAdmin(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("get_product")
    fun getProduct(): Call<ProductResponse>

    @POST("admin/add_product")
    fun addProduct(
        @Body addProductRequest: AddProductRequest
    ): Call<AddProductResponse>

    @HTTP(method = "DELETE", path = "admin/delete_product", hasBody = true)
    fun deleteProduct(
        @Body deleteProductRequest: DeleteProductRequest
    ): Call<DeleteProductResponse>

    @PUT("admin/edit_product")
    fun editProduct(
        @Body editProductRequest: EditProductRequest
    ): Call<EditProductResponse>

    @GET("admin/get_history")
    fun getHistory(): Call<TransactionResponse>

    @HTTP(method = "DELETE", path = "admin/delete_history", hasBody = true)
    fun deleteHistory(
        @Body deleteHistoryRequest: DeleteHistoryRequest
    ): Call<DeleteHistoryResponse>

    @PUT("admin/edit_history")
    fun editHistory(
        @Body editHistoryRequest: EditHistoryRequest
    ): Call<EditHistoryResponse>

    @GET("admin/get_oneWeeks")
    fun getOneWeeks(): Call<OneWeeksResponse>

    @GET("admin/get_oneMonths")
    fun getOneMonths(): Call<OneMonthResponse>

    @GET("admin/detail_transaksi/{id_transaksi}")
    fun getDetailTransaksi(
        @Path("id_transaksi") idTransaksi: String
    ): Call<DetailTransaksiResponse>

    @GET("get_rules_komplain")
    fun getRulesKomplain(): Call<RulesKomplainResponse>

    @HTTP(method = "DELETE", path = "admin/delete_rules_komplain", hasBody = true)
    fun deleteRulesKomplain(
        @Body deleteRulesKomplainRequest: DeleteRulesKomplainRequest
    ): Call<DeleteRulesKomplainResponse>

    @POST("admin/add_rules_komplain")
    fun addRulesKomplain(
        @Body addRulesKomplainRequest: AddRulesKomplainRequest
    ): Call<AddRulesKomplainResponse>

    @PUT("admin/edit_rules_komplain")
    fun editRulesKomplain(
        @Body editRulesKomplainRequest: EditRulesKomplainRequest
    ): Call<EditRulesKomplainResponse>

    @GET("get_rules_asosiasi")
    fun getRulesAsosiasi(): Call<RulesAsosiasiResponse>

    @HTTP(method = "DELETE", path = "admin/delete_rules_asosiasi", hasBody = true)
    fun deleteRulesAsosiasi(
        @Body deleteRulesAsosiasiRequest: DeleteRulesAsosiasiRequest
    ): Call<DeleteRulesAsosiasiResponse>

    @POST("admin/add_rules_asosiasi")
    fun addRulesAsosiasi(
        @Body addRulesAsosiasiRequest: AddRulesAsosiasiRequest
    ): Call<AddRulesAsosiasiResponse>

    @PUT("admin/edit_rules_asosiasi")
    fun editRulesAsosiasi(
        @Body editRulesAsosiasiRequest: EditRulesAsosiasiRequest
    ): Call<EditRulesAsosiasiResponse>

    @GET("get_category")
    fun getCategory(): Call<GetCategoryResponse>

    @PUT("admin/edit_category")
    fun editCategory(
        @Body editCategoryRequest: EditCategoryRequest
    ): Call<CategoryResponse>

    @HTTP(method = "DELETE", path = "admin/delete_category", hasBody = true)
    fun deleteCategory(
        @Body deleteCategoryRequest: DeleteCategoryRequest
    ): Call<CategoryResponse>

    @POST("admin/add_category")
    fun addCategory(
        @Body addCategoryRequest: AddCategoryRequest
    ): Call<CategoryResponse>

    @POST("admin/update_fcm")
    fun updateFcm(
        @Body updateFcmRequest: UpdateFcmRequest
    ): Call<UpdateFcmResponse>
}