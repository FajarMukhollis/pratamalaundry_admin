package com.fajar.pratamalaundry_admin.presentation.transaction

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
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
import com.fajar.pratamalaundry_admin.model.response.DeleteHistoryResponse
import com.fajar.pratamalaundry_admin.model.response.TransactionResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.TransactionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

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
        observeTransactionData()
        initRecyclerView()
        transactionViewModel.getTransaction()
        setActionBar()

        val swipeRefresh = _binding.swipeRefreshLayout
        swipeRefresh.setOnRefreshListener {
            transactionViewModel.getTransaction()
            transactionViewModel.transaction.observe(this) { transaction ->
                transactionAdapter.setData(transaction)
            }
            swipeRefresh.isRefreshing = false
        }
    }

    private fun showTransaction() {
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
        transactionAdapter.setOnDeleteClickListener(object :
            TransactionAdapter.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                showDeleteConfirmationDialog(position)
            }
        })

        transactionAdapter.setOnEditClickListener(object : TransactionAdapter.OnEditClickListener {
            override fun onEditClick(position: Int) {
                showEditProductForm(position)
            }
        })

        transactionAdapter.setOnItemClickListener(object : TransactionAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val idTransaksi = transactionAdapter.getItem(position)
                val intent = Intent(this@TransactionActivity, DetailTransactionActivity::class.java)
                intent.putExtra("id_transaksi", idTransaksi.id_transaksi)
                startActivity(intent)
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

    private fun showLoading(loading: Boolean) {
        when (loading) {
            true -> _binding.progressBar.visibility = View.VISIBLE
            false -> _binding.progressBar.visibility = View.GONE
        }
    }

    private fun showDatePicker(etTanggal: EditText, endDate: String) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                etTanggal.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val history = transactionAdapter.getItem(position)
        AlertDialog.Builder(this)
            .setTitle("Hapus Riwayat")
            .setMessage("Apa anda yakin untuk menghapus data ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                transactionViewModel.deleteTransaction(history.id_transaksi)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditProductForm(position: Int) {
        val history = transactionAdapter.getItem(position)
        val orderDate = history.tgl_order
        val editHistoryDialog = AlertDialog.Builder(this)
            .setTitle("Edit Riwayat")
            .setView(R.layout.dialog_edit_history)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        editHistoryDialog.setOnShowListener { dialog ->
            val saveButton = editHistoryDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val spStatusBayar = editHistoryDialog.findViewById<Spinner>(R.id.sp_status_bayar)
            val spStatusBarang = editHistoryDialog.findViewById<Spinner>(R.id.sp_status_barang)
            val etTanggal = editHistoryDialog.findViewById<EditText>(R.id.et_tanggal)
            val statusBayar = resources.getStringArray(R.array.status_bayar)
            val statusBarang = resources.getStringArray(R.array.status_barang)
            etTanggal?.setText(history.tgl_selesai)

            if (spStatusBayar != null) {
                val statusBayarAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    statusBayar
                )
                spStatusBayar.adapter = statusBayarAdapter

                val initialStatusBayar = history.status_bayar
                val initialStatusBayarPosition = statusBayar.indexOf(initialStatusBayar)
                if (initialStatusBayarPosition >= 0) {
                    spStatusBayar.setSelection(initialStatusBayarPosition)
                }
            }

            if (spStatusBarang != null) {
                val statusBarangAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    statusBarang
                )
                spStatusBarang.adapter = statusBarangAdapter

                val initialStatusBarang = history.status_barang
                val initialStatusBarangPosition = statusBarang.indexOf(initialStatusBarang)
                if (initialStatusBarangPosition >= 0) {
                    spStatusBarang.setSelection(initialStatusBarangPosition)
                }
            }

            etTanggal?.setOnClickListener {
                showDatePicker(etTanggal, orderDate)
                saveButton.isEnabled = true
            }

            saveButton.setOnClickListener {
                val selectedStatusBayar = spStatusBayar?.selectedItem.toString()
                val selectedStatusBarang = spStatusBarang?.selectedItem.toString()
                val selectedTanggal = etTanggal?.text.toString()

                if (selectedTanggal >= orderDate) {
                    transactionViewModel.putHistory(
                        history.id_transaksi,
                        selectedStatusBayar,
                        selectedStatusBarang,
                        selectedTanggal
                    )
                    editHistoryDialog.dismiss()
                } else {
                    saveButton.isEnabled = false
                    Toast.makeText(
                        this,
                        "Tanggal yang anda masukan lebih kecil dari tanggal order",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        editHistoryDialog.show()
    }

    private fun observeTransactionData() {
        transactionViewModel.transaction.observe(this) { transaction ->
            transactionAdapter.setData(transaction)
        }
    }

}