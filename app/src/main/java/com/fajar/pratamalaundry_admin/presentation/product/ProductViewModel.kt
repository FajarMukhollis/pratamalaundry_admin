package com.fajar.pratamalaundry_admin.presentation.product

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.DialogLoadingBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.DeleteProductRequest
import com.fajar.pratamalaundry_admin.model.request.EditProductRequest
import com.fajar.pratamalaundry_admin.model.response.DeleteProductResponse
import com.fajar.pratamalaundry_admin.model.response.EditProductResponse
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel : ViewModel() {

    private val _products = MutableLiveData<List<ProductResponse.Product>>()
    val products: LiveData<List<ProductResponse.Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchProducts() {
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
                    _products.value = response.body()?.data
                    _errorMessage.value = "Berhasil menampilkan produk"
                    showLoading(false)
                } else {
                    _errorMessage.value = "Gagal menampilkan produk"
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                showLoading(false)
                _errorMessage.value = "Gagal memuat data: ${t.message}"
            }
        })
    }

//    private fun showData(data: ProductResponse) {
//        val results = data.data
//        adapterProduct.setData(results)
//    }

    fun deleteProduct(id_product: String) {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val request = DeleteProductRequest(
            id_product = id_product
        )
        val call = retroInstance.deleteProduct(request)
        call.enqueue(object : Callback<DeleteProductResponse> {
            override fun onResponse(
                call: Call<DeleteProductResponse>,
                response: Response<DeleteProductResponse>
            ) {
                if (response.isSuccessful) {
                    _errorMessage.value = "Produk BERHASIL Di Hapus"
                    val newDataList = _products.value?.toMutableList()
                    newDataList?.removeAll { it.id_product == id_product }
                    _products.value = newDataList!!
                } else {
                    // handle failure
                    showLoading(false)
                    _errorMessage.value = "Gagal menghapus data"
                }
                showLoading(false)
            }

            override fun onFailure(call: Call<DeleteProductResponse>, t: Throwable) {
                // handle failure
                showLoading(false)
                _errorMessage.value = "Gagal menghapus data: ${t.message}"
            }
        })
    }

    fun editProduct(
        id_produk: String,
        id_kategori: String,
        nama_produk: String,
        durasi: String,
        harga_produk: String,
        satuan: String
    ) {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val request = EditProductRequest(
            id_produk = id_produk,
            id_kategori = id_kategori,
            nama_produk = nama_produk,
            durasi = durasi,
            harga_produk = harga_produk,
            satuan = satuan
        )
        val call = retroInstance.editProduct(request)
        call.enqueue(object : Callback<EditProductResponse> {
            override fun onResponse(
                call: Call<EditProductResponse>,
                response: Response<EditProductResponse>
            ) {
                if (response.isSuccessful) {
                    // handle success
                    _errorMessage.value = "Produk BERHASIL Di Ubah"
                    fetchProducts()
                } else {
                    // handle failure
                    _errorMessage.value = "GAGAL mengubah data"
                }
            }

            override fun onFailure(call: Call<EditProductResponse>, t: Throwable) {
                // handle failure
                showLoading(false)
                _errorMessage.value = "Gagal mengubah data: ${t.message}"
            }
        })
    }

    fun showLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}
