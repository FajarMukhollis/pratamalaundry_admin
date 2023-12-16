package com.fajar.pratamalaundry_admin.presentation.rules

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
import com.fajar.pratamalaundry_admin.databinding.ActivityRulesKomplainBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.AddRulesKomplainRequest
import com.fajar.pratamalaundry_admin.model.request.DeleteRulesKomplainRequest
import com.fajar.pratamalaundry_admin.model.request.EditRulesKomplainRequest
import com.fajar.pratamalaundry_admin.model.response.AddRulesKomplainResponse
import com.fajar.pratamalaundry_admin.model.response.DeleteRulesKomplainResponse
import com.fajar.pratamalaundry_admin.model.response.EditRulesKomplainResponse
import com.fajar.pratamalaundry_admin.model.response.RulesKomplainResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.RulesKomplainAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RulesKomplainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityRulesKomplainBinding
    private lateinit var adapterRulesKomplain: RulesKomplainAdapter
    private var selectedItemPosition: Int = -1
    private lateinit var selectedRules: RulesKomplainResponse.DataRulesKomplain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRulesKomplainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setActionBar()
        hideFab()
        initRecyclerView()
        getDataRulesKomplain()
        addRules()

        val swipeRefresh = _binding.swipeRefreshLayout
        swipeRefresh.setOnRefreshListener {
            getDataRulesKomplain()
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
        val rv = _binding.recyclerRules

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
        val rvRulesKomplain = _binding.recyclerRules
        adapterRulesKomplain = RulesKomplainAdapter(arrayListOf())
        adapterRulesKomplain.setOnItemClickListener(object :
            RulesKomplainAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                showDeleteRulesForm(position)
            }
        })

        adapterRulesKomplain.setOnEditClickListener(object :
            RulesKomplainAdapter.OnEditClickListener {
            override fun onEditClick(position: Int) {
                selectedItemPosition = position
                selectedRules = adapterRulesKomplain.getItem(position)
                showEditRulesForm(position)
            }
        })

        rvRulesKomplain.apply {
            layoutManager = LinearLayoutManager(this@RulesKomplainActivity)
            val decoration =
                DividerItemDecoration(this@RulesKomplainActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = adapterRulesKomplain
            setHasFixedSize(true)
        }
    }

    private fun getDataRulesKomplain() {
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getRulesKomplain()
        call.enqueue(object : Callback<RulesKomplainResponse> {
            override fun onResponse(
                call: Call<RulesKomplainResponse>,
                response: Response<RulesKomplainResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val dataRules = response.body()?.data
                    if (dataRules != null) {
                        adapterRulesKomplain.setData(dataRules)
                    }
                    Toast.makeText(
                        this@RulesKomplainActivity,
                        "Data Berhasil DiTemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showLoading(false)
                    Toast.makeText(
                        this@RulesKomplainActivity,
                        "Data Tidak Ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<RulesKomplainResponse>, t: Throwable) {
                Toast.makeText(
                    this@RulesKomplainActivity,
                    "Periksa Koneksi Internet Anda",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun showEditRulesForm(position: Int) {
        val editRulesDialog = AlertDialog.Builder(this)
            .setTitle("Edit Rules Komplain")
            .setView(R.layout.dialog_edit_rules)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        editRulesDialog.setOnShowListener { dialog ->
            val idRules = editRulesDialog.findViewById<TextView>(R.id.tvIdRules)
            val etRules = editRulesDialog.findViewById<EditText>(R.id.et_rules)

            idRules?.text = selectedRules.idRulesKomplain
            etRules?.setText(selectedRules.aturan)

            val saveButton = editRulesDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val idRules = selectedRules.idRulesKomplain
                val rules = etRules?.text.toString()

                val req = EditRulesKomplainRequest(
                    idRules,
                    rules
                )

                if (rules.isNotEmpty() && idRules.isNotEmpty()) {
                    val retroInstance = ApiConfig.getApiService()
                    val call = retroInstance.editRulesKomplain(req)
                    call.enqueue(object : Callback<EditRulesKomplainResponse> {
                        override fun onResponse(
                            call: Call<EditRulesKomplainResponse>,
                            response: Response<EditRulesKomplainResponse>
                        ) {
                            showLoading(false)
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@RulesKomplainActivity,
                                    "Data Berhasil Diubah",
                                    Toast.LENGTH_SHORT
                                ).show()
                                getDataRulesKomplain()
                            } else {
                                showLoading(false)
                                Toast.makeText(
                                    this@RulesKomplainActivity,
                                    "Data Gagal Diubah",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<EditRulesKomplainResponse>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                this@RulesKomplainActivity,
                                "Periksa Koneksi Internet Anda",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this@RulesKomplainActivity,
                        "Isi Kolom Terlebih Dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        editRulesDialog.show()
    }

    private fun showDeleteRulesForm(position: Int) {
        val rules = adapterRulesKomplain.getItem(position)
        AlertDialog.Builder(this)
            .setTitle("Hapus Rules Komplain")
            .setMessage("Apakah Anda Yakin Ingin Menghapus Aturan Komplain No. ${rules.idRulesKomplain}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteRules(rules.idRulesKomplain)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteRules(position: String) {
        showLoading(true)
        val req = DeleteRulesKomplainRequest(
            id_rules_komplain = position
        )

        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.deleteRulesKomplain(req)
        call.enqueue(object : Callback<DeleteRulesKomplainResponse> {
            override fun onResponse(
                call: Call<DeleteRulesKomplainResponse>,
                response: Response<DeleteRulesKomplainResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@RulesKomplainActivity,
                        "Data Berhasil Di Hapus",
                        Toast.LENGTH_SHORT
                    ).show()
                    getDataRulesKomplain()
                } else {
                    Toast.makeText(
                        this@RulesKomplainActivity,
                        "Data Gagal Di Hapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<DeleteRulesKomplainResponse>, t: Throwable) {
                Toast.makeText(
                    this@RulesKomplainActivity,
                    "Periksa Koneksi Internet Anda",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

    private fun addRules() {
        val fabAdd = _binding.fabAdd
        fabAdd.setOnClickListener {
            val addRulesDialog = AlertDialog.Builder(this)
                .setTitle("Edit Rules Komplain")
                .setView(R.layout.dialog_add_rules)
                .setPositiveButton("Tambahkan", null)
                .setNegativeButton("Batal", null)
                .create()

            addRulesDialog.setOnShowListener { dialog ->
                val saveButton = addRulesDialog.getButton(AlertDialog.BUTTON_POSITIVE)

                saveButton.setOnClickListener {
                    val etAturan = addRulesDialog.findViewById<EditText>(R.id.et_rules)
                    val aturan = etAturan?.text.toString()
                    val req = AddRulesKomplainRequest(
                        aturan = aturan
                    )
                    if (aturan.isNotEmpty()) {
                        val retroInstance = ApiConfig.getApiService()
                        val call = retroInstance.addRulesKomplain(req)
                        call.enqueue(object : Callback<AddRulesKomplainResponse> {
                            override fun onResponse(
                                call: Call<AddRulesKomplainResponse>,
                                response: Response<AddRulesKomplainResponse>
                            ) {
                                showLoading(false)
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@RulesKomplainActivity,
                                        "Data Berhasil Di Tambahkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    getDataRulesKomplain()
                                } else {
                                    Toast.makeText(
                                        this@RulesKomplainActivity,
                                        "Data Gagal Di Tambahkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<AddRulesKomplainResponse>, t: Throwable) {
                                Toast.makeText(
                                    this@RulesKomplainActivity,
                                    "Periksa Koneksi Internet Anda",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this@RulesKomplainActivity,
                            "Isi Kolom Terlebih Dahulu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            addRulesDialog.show()
        }
    }

    private fun showLoading(loading: Boolean) {
        _binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}