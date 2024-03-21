package com.fajar.pratamalaundry_admin.presentation.transaction

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.fajar.pratamalaundry_admin.databinding.ActivityDetailTransactionBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.response.DetailTransaksiResponse
import retrofit2.*

class DetailTransactionActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityDetailTransactionBinding

    private var baseUrl = "http://192.168.1.9/api-laundry"
//    private var baseUrl = "http://192.168.175.128/api-laundry" //hp
//    private var baseUrl = "https://pratamalaundry.my.id/" //hosting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val idTransaksi = intent.getStringExtra("id_transaksi").toString()

        showDataDetail(idTransaksi)
        _binding.tvNoTelp.setOnClickListener {
            val phoneNumber = _binding.tvNoTelp.text.toString()

            // Format nomor telepon untuk menghapus karakter selain angka
            val formattedPhoneNumber = phoneNumber.replace("[^0-9]".toRegex(), "")

            // Periksa apakah nomor dimulai dengan 0
            if (formattedPhoneNumber.startsWith("0")) {
                // Hilangkan 0 dan tambahkan kode negara Indonesia (62)
                val phoneNumberWithoutZero = formattedPhoneNumber.substring(1)
                val whatsappUrl = "https://wa.me/62$phoneNumberWithoutZero"

                // Buat intent untuk membuka aplikasi WhatsApp
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(whatsappUrl)

                // Periksa apakah aplikasi WhatsApp terinstall
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    // Handle jika aplikasi WhatsApp tidak terinstall
                    Toast.makeText(
                        this@DetailTransactionActivity,
                        "Aplikasi WhatsApp tidak terinstall.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Jika nomor tidak dimulai dengan 0, langsung buka WhatsApp
                val whatsappUrl = "https://wa.me/$formattedPhoneNumber"

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(whatsappUrl)

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@DetailTransactionActivity,
                        "Aplikasi WhatsApp tidak terinstall.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

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
                        _binding.tvNoPesanan.text = it.no_pesanan
                        _binding.tvNameCustomer.text = it.namaPelanggan
                        _binding.tvNoTelp.text = it.no_telp
                        _binding.tvAlamat.text = it.alamatPelanggan
                        _binding.tvNameCategory.text = it.jenisKategori
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
                            .load("$baseUrl/img_payment/${it.buktiBayar}")
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