package com.fajar.pratamalaundry_admin.model.request

data class EditProductRequest(
    val id_produk: String,
    val kategori: String,
    val nama_produk: String,
    val jenis_service: String,
    val durasi: String,
    val harga_produk: String,
    val satuan: String
)