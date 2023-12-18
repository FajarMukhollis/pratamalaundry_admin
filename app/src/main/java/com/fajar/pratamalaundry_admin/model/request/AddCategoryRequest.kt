package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class AddCategoryRequest(
    @SerializedName("jenis_kategori")
    val jenisKategori: String
)
