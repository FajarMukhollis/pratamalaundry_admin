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
import com.fajar.pratamalaundry_admin.databinding.ActivityRulesAsosiasiBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.AddRulesAsosiasiRequest
import com.fajar.pratamalaundry_admin.model.request.DeleteRulesAsosiasiRequest
import com.fajar.pratamalaundry_admin.model.request.EditRulesAsosiasiRequest
import com.fajar.pratamalaundry_admin.model.response.AddRulesAsosiasiResponse
import com.fajar.pratamalaundry_admin.model.response.DeleteRulesAsosiasiResponse
import com.fajar.pratamalaundry_admin.model.response.EditRulesAsosiasiResponse
import com.fajar.pratamalaundry_admin.model.response.RulesAsosiasiResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.RulesAsosiasiAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RulesAsosiasiActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityRulesAsosiasiBinding
    private lateinit var rulesAsosiasiAdapter: RulesAsosiasiAdapter
    private var selectedItemPosition: Int = -1
    private lateinit var selectedRules: RulesAsosiasiResponse.DataRulesAsosiasi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRulesAsosiasiBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setActionBar()
        hideFab()
        initRecyclerView()
        getDataRulesAsosiasi()
        addRules()

        val swipeRefresh = _binding.swipeRefreshLayout
        swipeRefresh.setOnRefreshListener {
            getDataRulesAsosiasi()
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
        val rvRulesAsosasi = _binding.recyclerRules
        rulesAsosiasiAdapter = RulesAsosiasiAdapter(arrayListOf())
        rulesAsosiasiAdapter.setOnItemClickListener(object :
            RulesAsosiasiAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                showDeleteRulesForm(position)
            }
        })

        rulesAsosiasiAdapter.setOnEditClickListener(object :
            RulesAsosiasiAdapter.OnEditClickListener {
            override fun onEditClick(position: Int) {
                selectedItemPosition = position
                selectedRules = rulesAsosiasiAdapter.getItem(position)
                showEditRulesForm(position)
            }
        })

        rvRulesAsosasi.apply {
            layoutManager = LinearLayoutManager(this@RulesAsosiasiActivity)
            val decoration =
                DividerItemDecoration(this@RulesAsosiasiActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = rulesAsosiasiAdapter
            setHasFixedSize(true)
        }
    }

    private fun getDataRulesAsosiasi() {
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getRulesAsosiasi()
        call.enqueue(object : Callback<RulesAsosiasiResponse> {
            override fun onResponse(
                call: Call<RulesAsosiasiResponse>,
                response: Response<RulesAsosiasiResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val dataRules = response.body()?.data
                    if (dataRules != null) {
                        rulesAsosiasiAdapter.setData(dataRules)
                    }
                    Toast.makeText(
                        this@RulesAsosiasiActivity,
                        "Data Berhasil DiTemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showLoading(false)
                    Toast.makeText(
                        this@RulesAsosiasiActivity,
                        "Data Tidak Ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<RulesAsosiasiResponse>, t: Throwable) {
                Toast.makeText(
                    this@RulesAsosiasiActivity,
                    "Periksa Koneksi Internet Anda",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

    private fun showEditRulesForm(position: Int) {
        val editRulesDialog = AlertDialog.Builder(this)
            .setTitle("Edit Aturan Asosiasi")
            .setView(R.layout.dialog_edit_rules)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        editRulesDialog.setOnShowListener { dialog ->
            val idRules = editRulesDialog.findViewById<TextView>(R.id.tvIdRules)
            val etRules = editRulesDialog.findViewById<EditText>(R.id.et_rules)

            idRules?.text = selectedRules.idRulesAsosiasi
            etRules?.setText(selectedRules.aturan)

            val saveButton = editRulesDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val idRules = selectedRules.idRulesAsosiasi
                val rules = etRules?.text.toString()

                val req = EditRulesAsosiasiRequest(
                    idRules,
                    rules
                )

                if (rules.isNotEmpty() && idRules.isNotEmpty()) {
                    val retroInstance = ApiConfig.getApiService()
                    val call = retroInstance.editRulesAsosiasi(req)
                    call.enqueue(object : Callback<EditRulesAsosiasiResponse> {
                        override fun onResponse(
                            call: Call<EditRulesAsosiasiResponse>,
                            response: Response<EditRulesAsosiasiResponse>
                        ) {
                            showLoading(false)
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@RulesAsosiasiActivity,
                                    "Data Berhasil Diubah",
                                    Toast.LENGTH_SHORT
                                ).show()
                                getDataRulesAsosiasi()
                            } else {
                                showLoading(false)
                                Toast.makeText(
                                    this@RulesAsosiasiActivity,
                                    "Data Gagal Diubah",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<EditRulesAsosiasiResponse>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                this@RulesAsosiasiActivity,
                                "Periksa Koneksi Internet Anda",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this@RulesAsosiasiActivity,
                        "Isi Kolom Terlebih Dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        editRulesDialog.show()
    }

    private fun showDeleteRulesForm(position: Int) {
        val rules = rulesAsosiasiAdapter.getItem(position)
        AlertDialog.Builder(this)
            .setTitle("Hapus Aturan Asosiasi")
            .setMessage("Apakah Anda Yakin Ingin Menghapus Aturan Komplain No. ${rules.idRulesAsosiasi}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteRules(rules.idRulesAsosiasi)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteRules(position: String) {
        showLoading(true)
        val req = DeleteRulesAsosiasiRequest(
            id_rules_asosiasi = position
        )

        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.deleteRulesAsosiasi(req)
        call.enqueue(object : Callback<DeleteRulesAsosiasiResponse> {
            override fun onResponse(
                call: Call<DeleteRulesAsosiasiResponse>,
                response: Response<DeleteRulesAsosiasiResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@RulesAsosiasiActivity,
                        "Data Berhasil Di Hapus",
                        Toast.LENGTH_SHORT
                    ).show()
                    getDataRulesAsosiasi()
                } else {
                    Toast.makeText(
                        this@RulesAsosiasiActivity,
                        "Data Gagal Di Hapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<DeleteRulesAsosiasiResponse>, t: Throwable) {
                Toast.makeText(
                    this@RulesAsosiasiActivity,
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
                .setTitle("Tambah Aturan Asosiasi")
                .setView(R.layout.dialog_add_rules)
                .setPositiveButton("Tambahkan", null)
                .setNegativeButton("Batal", null)
                .create()

            addRulesDialog.setOnShowListener { dialog ->
                val saveButton = addRulesDialog.getButton(AlertDialog.BUTTON_POSITIVE)

                saveButton.setOnClickListener {
                    val etAturan = addRulesDialog.findViewById<EditText>(R.id.et_rules)
                    val aturan = etAturan?.text.toString()
                    val req = AddRulesAsosiasiRequest(
                        aturan = aturan
                    )
                    if (aturan.isNotEmpty()) {
                        val retroInstance = ApiConfig.getApiService()
                        val call = retroInstance.addRulesAsosiasi(req)
                        call.enqueue(object : Callback<AddRulesAsosiasiResponse> {
                            override fun onResponse(
                                call: Call<AddRulesAsosiasiResponse>,
                                response: Response<AddRulesAsosiasiResponse>
                            ) {
                                showLoading(false)
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@RulesAsosiasiActivity,
                                        "Data Berhasil Di Tambahkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    getDataRulesAsosiasi()
                                } else {
                                    Toast.makeText(
                                        this@RulesAsosiasiActivity,
                                        "Data Gagal Di Tambahkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<AddRulesAsosiasiResponse>,
                                t: Throwable
                            ) {
                                Toast.makeText(
                                    this@RulesAsosiasiActivity,
                                    "Periksa Koneksi Internet Anda",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this@RulesAsosiasiActivity,
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