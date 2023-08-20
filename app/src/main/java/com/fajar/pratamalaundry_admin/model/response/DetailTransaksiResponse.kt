package com.fajar.pratamalaundry_admin.model.response

import com.google.gson.annotations.SerializedName

data class DetailTransaksiResponse(
    @SerializedName("data")
    val data: DetailDataTransaction,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Boolean
) {
    data class DetailDataTransaction(

        @SerializedName("id_transaksi")
        val idTransaksi: String,

        @SerializedName("id_pelanggan")
        val idPelanggan: String,

        @SerializedName("id_petugas")
        val idPetugas: String,

        @SerializedName("id_produk")
        val idProduk: String,

        @SerializedName("service")
        val service: String,

        @SerializedName("berat")
        val berat: String,

        @SerializedName("alamat_pelanggan")
        val alamatPelanggan: String,

        @SerializedName("total_harga")
        val totalHarga: String,

        @SerializedName("status_bayar")
        val statusBayar: String,

        @SerializedName("status_barang")
        val statusBarang: String,

        @SerializedName("tgl_order")
        val tglOrder: String,

        @SerializedName("tgl_selesai")
        val tglSelesai: String,

        @SerializedName("komplen")
        val komplen: String,

        @SerializedName("bukti_bayar")
        val buktiBayar: String,

        @SerializedName("nama_pelanggan")
        val namaPelanggan: String,

        @SerializedName("nama_produk")
        val namaProduk: String,

        )
}



