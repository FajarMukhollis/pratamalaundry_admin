package com.fajar.pratamalaundry_admin.presentation.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityAddProductBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.AddProductRequest
import com.fajar.pratamalaundry_admin.model.response.AddProductResponse
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAddProductBinding
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        setActionBar()
        productViewModel.fetchProducts()
        showLoading(false)

        _binding.btnAdd.setOnClickListener {
            addProduct()
        }
    }

    private fun setActionBar() {
        setSupportActionBar(_binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addProduct(){
        showLoading(true)
        val kategori = _binding.etKategori.text.toString()
        val namaproduk = _binding.etNamaProduk.text.toString()
        val serviceProduk = _binding.etServiceProduk.text.toString()
        val durasi = _binding.etDurasi.text.toString()
        val harga = _binding.etHarga.text.toString()
        val satuan = _binding.etSatuan.text.toString()
        val addProduct = AddProductRequest(
            kategori = kategori,
            nama_produk = namaproduk,
            jenis_service = serviceProduk,
            durasi = durasi,
            harga_produk = harga,
            satuan = satuan
        )

        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.addProduct(addProduct)
        call.enqueue(object: Callback<AddProductResponse>{
            override fun onResponse(
                call: Call<AddProductResponse>,
                response: Response<AddProductResponse>
            ) {
                showLoading(false)
                if(response.isSuccessful){
                    Toast.makeText(
                        this@AddProductActivity,
                        "Data Berhasil Ditambahkan",
                        Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@AddProductActivity, "Data Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
                }
                finish()
                productViewModel.fetchProducts()
            }

            override fun onFailure(call: Call<AddProductResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@AddProductActivity, "Data Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showLoading(loading: Boolean) {
        _binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}