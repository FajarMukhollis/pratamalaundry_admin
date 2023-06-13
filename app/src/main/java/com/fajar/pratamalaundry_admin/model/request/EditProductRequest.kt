package com.fajar.pratamalaundry_admin.model.request

data class EditProductRequest(
    val id_produk: String,
    val nama_produk: String,
    val jenis_service: String,
    val harga_produk: String
)