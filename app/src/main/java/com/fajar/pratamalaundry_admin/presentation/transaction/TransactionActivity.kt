package com.fajar.pratamalaundry_admin.presentation.transaction

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

    private lateinit var _binding: ActivityTransactionBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        transactionViewModel.isLoading.observe(this, Observer { Loading ->
            showLoading(Loading)
        })


        initRecyclerView()
        showTransaction()
        observeTransactionData()
        setActionBar()

    }

    private fun showTransaction(){
        transactionViewModel.getTransaction()
        transactionViewModel.errorMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
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

    private fun showData(data: TransactionResponse) {
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
            .setMessage("Apakah Kamu yakin untuk menghapus data transaksi ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                transactionViewModel.deleteTransaction(history.id_transaksi)
                dialog.dismiss()
                transactionViewModel.errorMessage.observe(this@TransactionActivity, Observer {
                    Toast.makeText(this@TransactionActivity, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                })
            }
            .setNegativeButton("Batal") { dialog, _ ->
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
//                val statusBayar = resources.getStringArray(R.array.status_bayar)
                val statusBarang = resources.getStringArray(R.array.status_barang)

                if (spStatusBayar != null) {
                    spStatusBayar.adapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.status_bayar,
                        android.R.layout.simple_spinner_dropdown_item
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spStatusBayar.adapter = adapter
                    }
                }

                if (spStatusBarang != null) {
                    spStatusBarang.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        statusBarang
                    )
                }

                etTanggal?.text
                spStatusBayar?.selectedItem.toString()
                spStatusBarang?.selectedItem.toString()

                transactionViewModel.putHistory(
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

    private fun observeTransactionData() {
        transactionViewModel.transaction.observe(this) { transaction ->
            transactionAdapter.setData(transaction)
        }
    }
}