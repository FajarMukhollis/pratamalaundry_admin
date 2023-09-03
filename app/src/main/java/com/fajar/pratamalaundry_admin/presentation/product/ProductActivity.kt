package com.fajar.pratamalaundry_admin.presentation.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityProductBinding
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.ProductAdapter

class ProductActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityProductBinding
    private lateinit var adapterProduct: ProductAdapter
    private var selectedItemPosition: Int = -1
    private lateinit var selectedProduct: ProductResponse.Product
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        productViewModel= ViewModelProvider(this).get(ProductViewModel::class.java)

        productViewModel.isLoading.observe(this, Observer { Loading ->
            showLoading(Loading)
        })

        val fabAdd = _binding.fabAdd
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        val swipeRefresh = _binding.swipeRefreshLayout
        swipeRefresh.setOnRefreshListener {
            productViewModel.fetchProducts()
            swipeRefresh.isRefreshing = false
        }

        initRecyclerView()
        observeProductData()
        productViewModel.fetchProducts()
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

    private fun observeProductData() {
        productViewModel.products.observe(this) { products ->
            adapterProduct.setData(products)
        }
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

    private fun initRecyclerView() {
        val rvProduct = _binding.recylerProduct
        adapterProduct = ProductAdapter(arrayListOf())
        adapterProduct.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                showDeleteConfirmationDialog(position)
            }
        })

        adapterProduct.setOnEditClickListener(object : ProductAdapter.OnEditClickListener {
            override fun onEditClick(position: Int) {
                selectedItemPosition = position
                selectedProduct = adapterProduct.getItem(position)
                showEditProductForm(position)
            }
        })

        rvProduct.apply {
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
                productViewModel.deleteProduct(product.id_product)
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
            val kategoriEditText = editProductDialog.findViewById<EditText>(R.id.et_kategori)
            val namaProdukEditText = editProductDialog.findViewById<EditText>(R.id.et_edit_nama_produk)
            val jenisServiceEditText = editProductDialog.findViewById<EditText>(R.id.et_edit_jenis_service)
            val durasiEditText = editProductDialog.findViewById<EditText>(R.id.et_durasi)
            val hargaProdukEditText = editProductDialog.findViewById<EditText>(R.id.et_edit_harga)
            val satuanEditText = editProductDialog.findViewById<EditText>(R.id.et_satuan)

            kategoriEditText?.setText(selectedProduct.kategori)
            namaProdukEditText?.setText(selectedProduct.nama_produk)
            jenisServiceEditText?.setText(selectedProduct.jenis_service)
            durasiEditText?.setText(selectedProduct.durasi)
            hargaProdukEditText?.setText(selectedProduct.harga_produk)
            satuanEditText?.setText(selectedProduct.satuan)

            val saveButton = editProductDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val kategori = kategoriEditText?.text.toString()
                val namaProduk = namaProdukEditText?.text.toString()
                val jenisService = jenisServiceEditText?.text.toString()
                val durasi = durasiEditText?.text.toString()
                val hargaProduk = hargaProdukEditText?.text.toString()
                val satuan = satuanEditText?.text.toString()

                if (kategori.isNotEmpty() && namaProduk.isNotEmpty() && jenisService.isNotEmpty() && durasi.isNotEmpty() && hargaProduk.isNotEmpty() && satuan.isNotEmpty()) {
                    productViewModel.editProduct(selectedProduct.id_product,kategori, namaProduk, jenisService, durasi, hargaProduk, satuan)
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

