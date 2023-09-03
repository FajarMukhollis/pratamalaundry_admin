package com.fajar.pratamalaundry_admin.model.request

data class AddProductRequest(
    val kategori: String,
    val nama_produk: String,
    val jenis_service: String,
    val durasi: String,
    val harga_produk: String,
    val satuan: String
)
