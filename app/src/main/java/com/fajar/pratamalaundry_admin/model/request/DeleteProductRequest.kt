package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class DeleteProductRequest(
    @SerializedName("id_produk")
    val id_product: String
)
