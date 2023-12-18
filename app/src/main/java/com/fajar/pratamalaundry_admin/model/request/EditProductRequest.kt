package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class EditProductRequest(
    @SerializedName("id_produk")
    val id_produk: String,

    @SerializedName("id_kategori")
    val id_kategori: String,

    @SerializedName("nama_produk")
    val nama_produk: String,

    @SerializedName("durasi")
    val durasi: String,

    @SerializedName("harga_produk")
    val harga_produk: String,

    @SerializedName("satuan")
    val satuan: String
)