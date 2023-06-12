package com.fajar.pratamalaundry_admin.presentation.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityAddProductBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.AddProductRequest
import com.fajar.pratamalaundry_admin.model.response.AddProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAddProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.btnAdd.setOnClickListener {
            addProduct()

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
                    Toast.makeText(this@AddProductActivity, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
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
}