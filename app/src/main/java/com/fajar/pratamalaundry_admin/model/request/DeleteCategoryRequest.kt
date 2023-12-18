package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class DeleteCategoryRequest(
    @SerializedName("id_kategori")
    val id_kategori: String
)
