package com.fajar.pratamalaundry_admin.presentation.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityProductBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.DeleteProductRequest
import com.fajar.pratamalaundry_admin.model.request.EditProductRequest
import com.fajar.pratamalaundry_admin.model.response.DeleteProductResponse
import com.fajar.pratamalaundry_admin.model.response.EditProductResponse
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.ProductAdapter
import retrofit2.*

class ProductActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityProductBinding
    private lateinit var adapterProduct: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val fabAdd = _binding.fabAdd
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        initRecyclerView()
        getDataFromApi()
        setActionBar()
        hideFab()
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

    private fun deleteProduct(id_product: String) {
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
                    Toast.makeText(
                        this@ProductActivity,
                        "BERHASIL MENGHAPUS DATA",
                        Toast.LENGTH_SHORT
                    ).show()
                    adapterProduct.removeItem(id_product)
                    showLoading(false)
                    getDataFromApi()
                } else {
                    Toast.makeText(
                        this@ProductActivity,
                        "GAGAL MENGHAPUS DATA",
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<DeleteProductResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@ProductActivity,
                    "Failed to delete product: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("TAG", "GAGAL HAPUS CUY: ${t.message}")
            }
        })
    }

    private fun editProduct(id_produk: String, nama_produk: String, jenis_service: String, harga_produk: String) {
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
                    Toast.makeText(
                        this@ProductActivity,
                        "BERHASIL MENGEDIT DATA",
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    getDataFromApi()
                } else {
                    Toast.makeText(
                        this@ProductActivity,
                        "GAGAL MENGEDIT DATA",
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<EditProductResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@ProductActivity,
                    "Failed to edit product: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("TAG", "GAGAL EDIT CUY: ${t.message}")
            }
        })
    }

    private fun hideFab() {
        val fab = _binding.fabAdd
        val rv = _binding.recylerProduct

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                super.onScrollStateChanged(rv, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0 && fab.isShown) {
                    fab.hide()
                } else if (dy < 0 && !fab.isShown) {
                    fab.show()
                }
            }
        })
    }

    private fun showData(data: ProductResponse) {
        val results = data.data
        adapterProduct.setData(results)
    }

    private fun initRecyclerView() {
        adapterProduct = ProductAdapter(arrayListOf())
        adapterProduct.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                showDeleteConfirmationDialog(position)
            }
        })

        adapterProduct.setOnEditClickListener(object : ProductAdapter.OnEditClickListener {
            override fun onEditClick(position: Int) {
                showEditProductForm(position)
            }
        })

        _binding.recylerProduct.apply {
            layoutManager = LinearLayoutManager(this@ProductActivity)
            val decoration = DividerItemDecoration(this@ProductActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = adapterProduct
            setHasFixedSize(true)
        }
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val product = adapterProduct.getItem(position)
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteProduct(product.id_product)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditProductForm(position: Int) {
        val product = adapterProduct.getItem(position)

        val editProductDialog = AlertDialog.Builder(this)
            .setTitle("Edit Product")
            .setView(R.layout.dialog_edit_product)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        editProductDialog.setOnShowListener { dialog ->
            val saveButton = editProductDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val namaProduk = editProductDialog.findViewById<EditText>(R.id.et_edit_nama_produk)?.text.toString()
                val jenisService = editProductDialog.findViewById<EditText>(R.id.et_edit_jenis_service)?.text.toString()
                val hargaProduk = editProductDialog.findViewById<EditText>(R.id.et_edit_harga)?.text.toString()

                if (namaProduk.isNotEmpty() && jenisService.isNotEmpty() && hargaProduk.isNotEmpty()) {
                    editProduct(product.id_product, namaProduk, jenisService, hargaProduk)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this@ProductActivity, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }

        editProductDialog.show()
    }

    private fun showLoading(loading: Boolean) {
        _binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}

