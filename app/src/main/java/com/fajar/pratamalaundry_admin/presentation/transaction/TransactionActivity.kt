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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
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
import com.fajar.pratamalaundry_admin.model.response.ProductResponse
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
    private val CHANNEL_ID = "Pratama Laundry"

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
        showTransaction()
        setActionBar()

        val swipeRefresh = _binding.swipeRefreshLayout
        swipeRefresh.setOnRefreshListener {
            showTransaction()
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

                // Set selected item if needed
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

                // Set selected item if needed
                val initialStatusBarang = history.status_barang
                val initialStatusBarangPosition = statusBarang.indexOf(initialStatusBarang)
                if (initialStatusBarangPosition >= 0) {
                    spStatusBarang.setSelection(initialStatusBarangPosition)
                }
            }

            saveButton.setOnClickListener {
                val selectedStatusBayar = spStatusBayar?.selectedItem.toString()
                val selectedStatusBarang = spStatusBarang?.selectedItem.toString()
                val selectedTanggal = etTanggal?.text.toString()

                transactionViewModel.putHistory(
                    history.id_transaksi,
                    selectedStatusBayar,
                    selectedStatusBarang,
                    selectedTanggal
                )
                editHistoryDialog.dismiss()
            }
        }
        editHistoryDialog.show()
    }

    private fun observeTransactionData() {
        transactionViewModel.transaction.observe(this) { transaction ->
            transactionAdapter.setData(transaction)
        }
    }

    private fun checkAndNotifyNewTransactions(transactions: List<TransactionResponse.Data>) {
        // Hitung jumlah transaksi sebelum dan sesudah pembaruan
        val oldCount = transactionAdapter.itemCount
        val newCount = transactions.size

        // Jika ada transaksi baru, tampilkan notifikasi
        if (newCount > oldCount) {
            showNotification("Transaksi Baru", "Anda memiliki pesanan baru.")
        }
    }

    private fun showNotification(title: String, message: String){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Pratama Laundry"
            val descriptionText = "Ada Pesanan Baru"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

}