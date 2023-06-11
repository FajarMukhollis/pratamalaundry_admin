package com.fajar.pratamalaundry_admin.presentation.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityProductBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityProductBinding
    private lateinit var adapterProduct : ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        initRecyclerView()
        getDataFromApi()

    }

    private fun getDataFromApi() {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getProduct()
        call.enqueue(object: Callback<ProductResponse> {
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

    private fun initRecyclerView() {
        adapterProduct = ProductAdapter(arrayListOf())
        _binding.rvProduct.apply {
            layoutManager = LinearLayoutManager(this@ProductActivity)
            val decoration = DividerItemDecoration(this@ProductActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = adapterProduct
            setHasFixedSize(true)
        }
    }

    private fun showLoading(loading: Boolean) {
        when(loading) {
            true -> _binding.progressBar.visibility = View.VISIBLE
            false -> _binding.progressBar.visibility = View.GONE
        }
    }
}