package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class AddProductRequest(
    @SerializedName("id_kategori")
    val idkategori: String,

    @SerializedName("nama_produk")
    val nama_produk: String,

    @SerializedName("durasi")
    val durasi: String,

    @SerializedName("harga_produk")
    val harga_produk: String,

    @SerializedName("satuan")
    val satuan: String
)
