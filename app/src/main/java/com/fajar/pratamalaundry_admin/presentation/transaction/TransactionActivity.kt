package com.fajar.pratamalaundry_admin.presentation.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityTransactionBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.DeleteHistoryRequest
import com.fajar.pratamalaundry_admin.model.request.EditHistoryRequest
import com.fajar.pratamalaundry_admin.model.response.DeleteHistoryResponse
import com.fajar.pratamalaundry_admin.model.response.EditHistoryResponse
import com.fajar.pratamalaundry_admin.model.response.TransactionResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.ProductAdapter
import com.fajar.pratamalaundry_admin.presentation.adapter.TransactionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionActivity : AppCompatActivity() {

    private val statusBayarItems = arrayListOf(
        "BELUM LUNAS",
        "LUNAS"
    )
    private val statusBarangItems = arrayListOf(
        "BELUM DI PROSES",
        "SEDANG DI PROSES",
        "SELESAI"
    )

    private lateinit var _binding: ActivityTransactionBinding
    private lateinit var transactionAdapter: TransactionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        initRecyclerView()
        getTransaction()
        setActionBar()

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

    private fun initRecyclerView() {
        transactionAdapter = TransactionAdapter(arrayListOf())
        transactionAdapter.setOnItemClickListener(object : TransactionAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                showDeleteConfirmationDialog(position)
            }
        })

        transactionAdapter.setOnEditClickListener(object : TransactionAdapter.OnEditClickListener {
            override fun onEditClick(position: Int) {
                showEditProductForm(position)
            }
        })


        _binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(this@TransactionActivity)
            val decoration =
                DividerItemDecoration(this@TransactionActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = transactionAdapter
            setHasFixedSize(true)
        }
    }

    private fun showData(data: TransactionResponse){
        val results = data.data
        transactionAdapter.setData(results)
    }

    private fun showLoading(loading: Boolean) {
        when (loading) {
            true -> _binding.progressBar.visibility = View.VISIBLE
            false -> _binding.progressBar.visibility = View.GONE
        }
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val history = transactionAdapter.getItem(position)
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteHistory(history.id_transaksi)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditProductForm(position: Int) {
        val history = transactionAdapter.getItem(position)
        val editHistoryDialog = AlertDialog.Builder(this)
            .setTitle("Edit History")
            .setView(R.layout.dialog_edit_history)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        editHistoryDialog.setOnShowListener { dialog ->
            val saveButton = editHistoryDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {

                val spStatusBayar = editHistoryDialog.findViewById<Spinner>(R.id.sp_status_bayar)
                val spStatusBarang = editHistoryDialog.findViewById<Spinner>(R.id.sp_status_barang)
                val etTanggal = editHistoryDialog.findViewById<EditText>(R.id.et_tanggal)


                if (spStatusBayar != null) {
                    spStatusBayar.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        statusBayarItems
                    )
                }

                if (spStatusBarang != null) {
                    spStatusBarang.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        statusBarangItems
                    )
                }

                etTanggal?.text
                spStatusBayar?.selectedItem.toString()
                spStatusBarang?.selectedItem.toString()

                putHistory(
                    history.id_transaksi,
                    spStatusBayar.toString(),
                    spStatusBayar.toString(),
                    etTanggal.toString()
                )

                editHistoryDialog.dismiss()
            }
        }
        return editHistoryDialog.show()
    }


    private fun getTransaction(){
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getHistory()
        call.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(
                call: Call<TransactionResponse>,
                response: Response<TransactionResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful){
                    showData(response.body()!!)
                    Toast.makeText(
                        this@TransactionActivity,
                        "Data Transaksi Pelanggan BERHASIL Di Temukan",
                        Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@TransactionActivity,
                        "Data Transaksi Pelanggan TIDAK Di Temukan",
                        Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                showLoading(false)
            }

        })
    }

    private fun deleteHistory(id_transaksi: String){
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val request = DeleteHistoryRequest(id_transaksi = id_transaksi)
        val call = retroInstance.deleteHistory(request)
        call.enqueue(object : Callback<DeleteHistoryResponse>{
            override fun onResponse(
                call: Call<DeleteHistoryResponse>,
                response: Response<DeleteHistoryResponse>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(
                        this@TransactionActivity,
                        "Data Transaksi Pelanggan BERHASIL Di Hapus",
                        Toast.LENGTH_LONG
                    ).show()
                    transactionAdapter.removeItem(id_transaksi)
                    showLoading(false)
                    getTransaction()
                } else {
                    Toast.makeText(
                        this@TransactionActivity,
                        "Data Transaksi Pelanggan TIDAK Di Hapus",
                        Toast.LENGTH_LONG
                    ).show()
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<DeleteHistoryResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@TransactionActivity,
                    "Failed to delete product: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("TAG", "GAGAL HAPUS CUY: ${t.message}")
            }

        })
    }

    private fun putHistory (
        id_transaksi:String,
        status_bayar: String,
        status_barang: String,
        tgl_selesai: String){

        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val request = EditHistoryRequest(
            id_transaksi = id_transaksi,
            status_bayar = status_bayar,
            status_barang = status_barang,
            tgl_selesai = tgl_selesai
        )
        val call = retroInstance.editHistory(request)
        call.enqueue(object : Callback<EditHistoryResponse>{
            override fun onResponse(
                call: Call<EditHistoryResponse>,
                response: Response<EditHistoryResponse>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(
                        this@TransactionActivity,
                        "Data Transaksi Pelanggan BERHASIL Di Ubah",
                        Toast.LENGTH_LONG
                    ).show()
                    showLoading(false)
                    getTransaction()
                } else {
                    Toast.makeText(
                        this@TransactionActivity,
                        "Data Transaksi Pelanggan GAGAL Di Ubah",
                        Toast.LENGTH_LONG
                    ).show()
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<EditHistoryResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@TransactionActivity,
                    "Failed to edit product: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("TAG", "GAGAL EDIT CUY: ${t.message}")
            }
        })
    }

}