package com.fajar.pratamalaundry_admin.presentation.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityDetailTransactionBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.DeleteHistoryRequest
import com.fajar.pratamalaundry_admin.model.response.DetailTransaksiResponse
import com.fajar.pratamalaundry_admin.model.response.TransactionResponse
import retrofit2.*

class DetailTransactionActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityDetailTransactionBinding
    //    private var baseUrl = "http://192.168.1.5/api-laundry"
    private var baseUrl = "https://pratamalaundry.my.id/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val idTransaksi = intent.getStringExtra("id_transaksi").toString()

        showDataDetail(idTransaksi)
    }

    private fun showDataDetail(id_transaksi: String) {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getDetailTransaksi(id_transaksi)
        call.enqueue(object : Callback<DetailTransaksiResponse> {
            override fun onResponse(
                call: Call<DetailTransaksiResponse>,
                response: Response<DetailTransaksiResponse>
            ) {
                if (response.isSuccessful) {
                    showLoading(false)
                    val detailTransaksi = response.body()?.data
                    detailTransaksi?.let {
                        _binding.tvNameCustomer.text = it.namaPelanggan
                        _binding.tvNoTelp.text = it.no_telp
                        _binding.tvAlamat.text = it.alamatPelanggan
                        _binding.tvNameProduct.text = it.namaProduk
                        _binding.tvService.text = it.service
                        _binding.tvTanggalOrder.text = it.tglOrder
                        _binding.tvTanggalSelesai.text = it.tglSelesai
                        _binding.tvStatusBayar.text = it.statusBayar
                        _binding.tvStatusBarang.text = it.statusBarang
                        _binding.tvBerat.text = it.berat
                        _binding.tvHarga.text = it.totalHarga
                        _binding.tvKomplen.text = it.komplen
                        Glide.with(this@DetailTransactionActivity)
                            .load("$baseUrl /img_payment/${it.buktiBayar}")
                            .into(_binding.imgPayment)
                    }
                    Toast.makeText(
                        this@DetailTransactionActivity,
                        "Sukses Memuat Data",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    showLoading(false)
                    Toast.makeText(
                        this@DetailTransactionActivity,
                        "Gagal Memuat Data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<DetailTransaksiResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@DetailTransactionActivity,
                    "Gagal Memuat Data",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun showLoading(loading: Boolean) {
        _binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}