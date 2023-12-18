package com.fajar.pratamalaundry_admin.presentation.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityCategoryBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.AddCategoryRequest
import com.fajar.pratamalaundry_admin.model.request.DeleteCategoryRequest
import com.fajar.pratamalaundry_admin.model.request.EditCategoryRequest
import com.fajar.pratamalaundry_admin.model.response.CategoryResponse
import com.fajar.pratamalaundry_admin.model.response.GetCategoryResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.CategoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityCategoryBinding
    private lateinit var adapterCategory: CategoryAdapter
    private var selectedItemPosition: Int = -1
    private lateinit var selectedRules: GetCategoryResponse.DataCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setActionBar()
        hideFab()
        initRecyclerView()
        getCategory()
        addCategory()

        val swipeRefresh = _binding.swipeRefreshLayout
        swipeRefresh.setOnRefreshListener {
            getCategory()
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

    private fun hideFab() {
        val fab = _binding.fabAdd
        val rv = _binding.recyclerCategory

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
        val rvCategory = _binding.recyclerCategory
        adapterCategory = CategoryAdapter(arrayListOf())
        adapterCategory.setOnItemClickListener(object :
            CategoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                showDeleteCategoryForm(position)
            }
        })

        adapterCategory.setOnEditClickListener(object :
            CategoryAdapter.OnEditClickListener {
            override fun onEditClick(position: Int) {
                selectedItemPosition = position
                selectedRules = adapterCategory.getItem(position)
                showEditCategoryForm(position)
            }
        })

        rvCategory.apply {
            layoutManager = LinearLayoutManager(this@CategoryActivity)
            val decoration =
                DividerItemDecoration(this@CategoryActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = adapterCategory
            setHasFixedSize(true)
        }
    }

    private fun getCategory() {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getCategory()
        call.enqueue(object : Callback<GetCategoryResponse> {
            override fun onResponse(
                call: Call<GetCategoryResponse>,
                response: Response<GetCategoryResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val dataRules = response.body()?.data
                    if (dataRules != null) {
                        adapterCategory.setData(dataRules)
                    }
                    Toast.makeText(
                        this@CategoryActivity,
                        "Data Berhasil DiTemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showLoading(false)
                    Toast.makeText(
                        this@CategoryActivity,
                        "Data Tidak Ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<GetCategoryResponse>, t: Throwable) {
                Toast.makeText(
                    this@CategoryActivity,
                    "Periksa Koneksi Internet Anda",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

    private fun showEditCategoryForm(position: Int) {
        val editCategory = AlertDialog.Builder(this)
            .setTitle("Edit Kategori")
            .setView(R.layout.dialog_edit_category)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        editCategory.setOnShowListener { dialog ->
            val idCategory = editCategory.findViewById<TextView>(R.id.tv_id_category)
            val etCategory = editCategory.findViewById<EditText>(R.id.et_category)

            idCategory?.text = selectedRules.id_kategori
            etCategory?.setText(selectedRules.jenis_kategori)

            val saveButton = editCategory.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val idCategory = selectedRules.id_kategori
                val category = etCategory?.text.toString()

                val req = EditCategoryRequest(
                    idCategory,
                    category
                )

                if (category.isNotEmpty() && idCategory.isNotEmpty()) {
                    val retroInstance = ApiConfig.getApiService()
                    val call = retroInstance.editCategory(req)
                    call.enqueue(object : Callback<CategoryResponse> {
                        override fun onResponse(
                            call: Call<CategoryResponse>,
                            response: Response<CategoryResponse>
                        ) {
                            showLoading(false)
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@CategoryActivity,
                                    "Data Berhasil Diubah",
                                    Toast.LENGTH_SHORT
                                ).show()
                                getCategory()
                            } else {
                                showLoading(false)
                                Toast.makeText(
                                    this@CategoryActivity,
                                    "Data Gagal Diubah",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<CategoryResponse>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                this@CategoryActivity,
                                "Periksa Koneksi Internet Anda",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this@CategoryActivity,
                        "Isi Kolom Terlebih Dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        editCategory.show()
    }

    private fun showDeleteCategoryForm(position: Int) {
        val rules = adapterCategory.getItem(position)
        AlertDialog.Builder(this)
            .setTitle("Hapus Aturan Asosiasi")
            .setMessage("Apakah Anda Yakin Ingin Menghapus Aturan Komplain No. ${rules.id_kategori}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteCategory(rules.id_kategori)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteCategory(position: String) {
        showLoading(true)
        val req = DeleteCategoryRequest(
            id_kategori = position
        )

        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.deleteCategory(req)
        call.enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(
                call: Call<CategoryResponse>,
                response: Response<CategoryResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@CategoryActivity,
                        "Data Berhasil Di Hapus",
                        Toast.LENGTH_SHORT
                    ).show()
                    getCategory()
                } else {
                    Toast.makeText(
                        this@CategoryActivity,
                        "Data Gagal Di Hapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Toast.makeText(
                    this@CategoryActivity,
                    "Periksa Koneksi Internet Anda",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

    private fun addCategory() {
        val fabAdd = _binding.fabAdd
        fabAdd.setOnClickListener {
            val addCategoryDialog = AlertDialog.Builder(this)
                .setTitle("Tambah Aturan Asosiasi")
                .setView(R.layout.dialog_add_category)
                .setPositiveButton("Tambahkan", null)
                .setNegativeButton("Batal", null)
                .create()

            addCategoryDialog.setOnShowListener { dialog ->
                val saveButton = addCategoryDialog.getButton(AlertDialog.BUTTON_POSITIVE)

                saveButton.setOnClickListener {
                    val etCategory = addCategoryDialog.findViewById<EditText>(R.id.et_category)
                    val category = etCategory?.text.toString()
                    val req = AddCategoryRequest(
                        jenisKategori = category
                    )
                    if (category.isNotEmpty()) {
                        val retroInstance = ApiConfig.getApiService()
                        val call = retroInstance.addCategory(req)
                        call.enqueue(object : Callback<CategoryResponse> {
                            override fun onResponse(
                                call: Call<CategoryResponse>,
                                response: Response<CategoryResponse>
                            ) {
                                showLoading(false)
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@CategoryActivity,
                                        "Data Berhasil Di Tambahkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    getCategory()
                                } else {
                                    Toast.makeText(
                                        this@CategoryActivity,
                                        "Data Gagal Di Tambahkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<CategoryResponse>,
                                t: Throwable
                            ) {
                                Toast.makeText(
                                    this@CategoryActivity,
                                    "Periksa Koneksi Internet Anda",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this@CategoryActivity,
                            "Isi Kolom Terlebih Dahulu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            addCategoryDialog.show()
        }
    }

    private fun showLoading(loading: Boolean) {
        _binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}