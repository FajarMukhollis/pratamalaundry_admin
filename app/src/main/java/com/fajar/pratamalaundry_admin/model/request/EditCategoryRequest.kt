package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class EditCategoryRequest(
    @SerializedName("id_kategori")
    val idKategori: String,

    @SerializedName("jenis_kategori")
    val jenisKategori: String
)
