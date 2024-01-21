package com.fajar.pratamalaundry_admin.presentation.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityProductBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.response.GetCategoryResponse
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.CategorySpinnerAdapter
import com.fajar.pratamalaundry_admin.presentation.adapter.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityProductBinding
    private lateinit var adapterProduct: ProductAdapter
    private var selectedItemPosition: Int = -1
    private lateinit var selectedProduct: ProductResponse.Product
    private lateinit var productViewModel: ProductViewModel
    private lateinit var categorySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        productViewModel.isLoading.observe(this, Observer { Loading ->
            showLoading(Loading)
        })

        val fabAdd = _binding.fabAdd
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        initRecyclerView()
        observeProductData()
        productViewModel.fetchProducts()
        setActionBar()
        hideFab()

        val swipeRefresh = _binding.swipeRefreshLayout
        swipeRefresh.setOnRefreshListener {
            productViewModel.fetchProducts()
            swipeRefresh.isRefreshing = false
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

    private fun observeProductData() {
        productViewModel.products.observe(this) { products ->
            adapterProduct.setData(products)
        }
    }


    private fun hideFab() {
        val fab = _binding.fabAdd
        val rv = _binding.recylerProduct

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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

    private fun initRecyclerView() {
        val rvProduct = _binding.recylerProduct
        adapterProduct = ProductAdapter(arrayListOf())
        adapterProduct.setOnItemClickListener(object :
            ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                showDeleteConfirmationDialog(position)
            }
        })

        adapterProduct.setOnEditClickListener(object :
            ProductAdapter.OnEditClickListener {
            override fun onEditClick(position: Int) {
                selectedItemPosition = position
                selectedProduct = adapterProduct.getItem(position)
                showEditProductForm(position)
            }
        })

        rvProduct.apply {
            layoutManager = LinearLayoutManager(this@ProductActivity)
            val decoration =
                DividerItemDecoration(this@ProductActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = adapterProduct
            setHasFixedSize(true)
        }
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val product = adapterProduct.getItem(position)
        AlertDialog.Builder(this)
            .setTitle("Hapus Produk")
            .setMessage("Apa anda yakin untuk menghapus data ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                productViewModel.deleteProduct(product.id_product)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditProductForm(position: Int) {
        val product = adapterProduct.getItem(position)

        val editProductDialog = AlertDialog.Builder(this)
            .setTitle("Edit Produk")
            .setView(R.layout.dialog_edit_product)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        editProductDialog.setOnShowListener { dialog ->
            val spKategori = editProductDialog.findViewById<Spinner>(R.id.sp_kategori)
            categorySpinner = spKategori ?: Spinner(this)
            showCategorySpinner(categorySpinner)


            val namaProdukEditText =
                editProductDialog.findViewById<EditText>(R.id.et_edit_nama_produk)
            val durasiEditText = editProductDialog.findViewById<EditText>(R.id.et_durasi)
            val hargaProdukEditText = editProductDialog.findViewById<EditText>(R.id.et_edit_harga)
            val satuanEditText = editProductDialog.findViewById<EditText>(R.id.et_satuan)

//            categorySpinner.setSelection(selectedItemPosition)
            spKategori?.setSelection(getCategoryPosition(product.id_kategori, categorySpinner))
            namaProdukEditText?.setText(selectedProduct.nama_produk)
            durasiEditText?.setText(selectedProduct.durasi)
            hargaProdukEditText?.setText(selectedProduct.harga_produk)
            satuanEditText?.setText(selectedProduct.satuan)

            val saveButton = editProductDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val selectedCategory = categorySpinner.selectedItem as? GetCategoryResponse.DataCategory
                val kategori = selectedCategory?.id_kategori ?: ""  //saya hanya ingin mengirimkan id_kategori nya saja
                val namaProduk = namaProdukEditText?.text.toString()
                val durasi = durasiEditText?.text.toString()
                val hargaProduk = hargaProdukEditText?.text.toString()
                val satuan = satuanEditText?.text.toString()
                Log.d("idKategori: ", kategori)
                if (namaProduk.isNotEmpty() && kategori.isNotEmpty() && durasi.isNotEmpty() && hargaProduk.isNotEmpty() && satuan.isNotEmpty()) {
                    productViewModel.editProduct(
                        selectedProduct.id_product,
                        kategori,
                        namaProduk,
                        durasi,
                        hargaProduk,
                        satuan
                    )
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this@ProductActivity,
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        editProductDialog.show()
    }

    private fun getCategoryPosition(idKategori: String, spinner: Spinner): Int {
        val adapter = spinner.adapter
        if (adapter != null) {
            val count = adapter.count
            for (i in 0 until count) {
                val category = adapter.getItem(i) as GetCategoryResponse.DataCategory
                if (category.id_kategori == idKategori) {
                    return i
                }
            }
        }
        return 0 // Mengembalikan nilai default jika adapter null atau kategori tidak ditemukan
    }

    private fun showCategorySpinner(spinner: Spinner){
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getCategory()
        call.enqueue(object : Callback<GetCategoryResponse>{
            override fun onResponse(
                call: Call<GetCategoryResponse>,
                response: Response<GetCategoryResponse>
            ) {
                if(response.isSuccessful){
                    val categoryResponse = response.body()
                    val category = categoryResponse?.data ?: emptyList()

                    val adapter = CategorySpinnerAdapter(this@ProductActivity, category)
                    categorySpinner.adapter = adapter

                    categorySpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener{
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
                                    this@ProductActivity,
                                    "Kamu tidak memilih kategori apapun",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                } else {
                    Toast.makeText(
                        this@ProductActivity,
                        "Gagal mendapatkan data produk",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GetCategoryResponse>, t: Throwable) {
                Toast.makeText(
                    this@ProductActivity,
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

