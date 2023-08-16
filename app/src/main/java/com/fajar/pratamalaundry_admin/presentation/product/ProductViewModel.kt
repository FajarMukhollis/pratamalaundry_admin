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
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                showLoading(false)
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
                    // handle success
                    fetchProducts()
                } else {
                    // handle failure
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<DeleteProductResponse>, t: Throwable) {
                // handle failure
                showLoading(false)
            }
        })
    }

    fun editProduct(id_produk: String, nama_produk: String, jenis_service: String, harga_produk: String) {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val request = EditProductRequest(
            id_produk = id_produk,
            nama_produk = nama_produk,
            jenis_service = jenis_service,
            harga_produk = harga_produk
        )
        val call = retroInstance.editProduct(request)
        call.enqueue(object : Callback<EditProductResponse> {
            override fun onResponse(
                call: Call<EditProductResponse>,
                response: Response<EditProductResponse>
            ) {
                if (response.isSuccessful) {
                    // handle success
                    fetchProducts()
                } else {
                    // handle failure
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<EditProductResponse>, t: Throwable) {
                // handle failure
                showLoading(false)
            }
        })
    }

    fun showLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}
