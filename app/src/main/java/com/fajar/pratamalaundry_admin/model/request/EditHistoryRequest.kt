package com.fajar.pratamalaundry_admin.model.request

data class EditHistoryRequest(
    val id_transaksi:String,
    val status_bayar: String,
    val status_barang: String,
    val tgl_selesai: String
)
