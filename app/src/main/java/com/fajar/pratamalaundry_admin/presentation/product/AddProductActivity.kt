package com.fajar.pratamalaundry_admin.presentation.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityAddProductBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.AddProductRequest
import com.fajar.pratamalaundry_admin.model.response.AddProductResponse
import com.fajar.pratamalaundry_admin.model.response.GetCategoryResponse
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.CategorySpinnerAdapter
import com.fajar.pratamalaundry_admin.presentation.adapter.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAddProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var categorySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        categorySpinner = _binding.spKategori

        setActionBar()
        productViewModel.fetchProducts()
        showCategorySpinner()
        showLoading(false)

        _binding.btnAdd.setOnClickListener {
            addProduct()
            productViewModel.fetchProducts()
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

    private fun addProduct() {
        showLoading(true)
        val selectedCategory = categorySpinner.selectedItem as GetCategoryResponse.DataCategory
        val kategori = selectedCategory.id_kategori
        val namaproduk = _binding.etNamaProduk.text.toString()
        val durasi = _binding.etDurasi.text.toString()
        val harga = _binding.etHarga.text.toString()
        val satuan = _binding.etSatuan.text.toString()
        Log.d("idKategori:", kategori)
        val addProduct = AddProductRequest(
            idkategori = kategori,
            nama_produk = namaproduk,
            durasi = durasi,
            harga_produk = harga,
            satuan = satuan
        )

        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.addProduct(addProduct)
        call.enqueue(object : Callback<AddProductResponse> {
            override fun onResponse(
                call: Call<AddProductResponse>,
                response: Response<AddProductResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@AddProductActivity,
                        "Data Berhasil Ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    productViewModel.fetchProducts()
                } else {
                    Toast.makeText(
                        this@AddProductActivity,
                        "Data Gagal Ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }

            override fun onFailure(call: Call<AddProductResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@AddProductActivity,
                    "Data Gagal Ditambahkan",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun showCategorySpinner() {
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getCategory()
        call.enqueue(object : Callback<GetCategoryResponse> {
            override fun onResponse(
                call: Call<GetCategoryResponse>,
                response: Response<GetCategoryResponse>
            ) {
                if (response.isSuccessful) {
                    val categoryResponse = response.body()
                    val category = categoryResponse?.data ?: emptyList()

                    val adapter = CategorySpinnerAdapter(this@AddProductActivity, category)
                    categorySpinner.adapter = adapter

                    categorySpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                category[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                Toast.makeText(
                                    this@AddProductActivity,
                                    "Kamu tidak memilih kategori apapun",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                } else {
                    Toast.makeText(
                        this@AddProductActivity,
                        "Gagal mendapatkan data produk",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GetCategoryResponse>, t: Throwable) {
                Toast.makeText(
                    this@AddProductActivity,
                    "Periksa Koneksi Internet Anda",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun showLoading(loading: Boolean) {
        _binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}