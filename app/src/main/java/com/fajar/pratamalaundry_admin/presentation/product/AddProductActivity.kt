package com.fajar.pratamalaundry_admin.presentation.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
    private lateinit var adapterProduct: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setActionBar()

        _binding.btnAdd.setOnClickListener {
            addProduct()
            getDataFromApi()
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
        val namaproduk = _binding.etNamaProduk
        val serviceproduk = _binding.etServiceProduk
        val harga = _binding.etHarga
        val addProduct = AddProductRequest(
            nama_produk = namaproduk.text.toString(),
            jenis_service = serviceproduk.text.toString(),
            harga_produk = harga.text.toString()
        )

        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.addProduct(addProduct)
        call.enqueue(object: Callback<AddProductResponse>{
            override fun onResponse(
                call: Call<AddProductResponse>,
                response: Response<AddProductResponse>
            ) {
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
            }

            override fun onFailure(call: Call<AddProductResponse>, t: Throwable) {
                Toast.makeText(this@AddProductActivity, "Data Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getDataFromApi() {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getProduct()
        call.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    showData(response.body()!!)
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                showLoading(false)
            }
        })
    }

    private fun showData(data: ProductResponse) {
        val results = data.data
        adapterProduct.setData(results)
    }

    private fun showLoading(loading: Boolean) {
        _binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}