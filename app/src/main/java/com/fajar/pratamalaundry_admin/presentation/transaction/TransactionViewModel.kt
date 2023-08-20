package com.fajar.pratamalaundry_admin.presentation.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.request.DeleteHistoryRequest
import com.fajar.pratamalaundry_admin.model.request.EditHistoryRequest
import com.fajar.pratamalaundry_admin.model.response.DeleteHistoryResponse
import com.fajar.pratamalaundry_admin.model.response.DetailTransaksiResponse
import com.fajar.pratamalaundry_admin.model.response.EditHistoryResponse
import com.fajar.pratamalaundry_admin.model.response.TransactionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionViewModel : ViewModel() {

    private val _transactions = MutableLiveData<List<TransactionResponse.Data>>()
    val transaction: MutableLiveData<List<TransactionResponse.Data>> = _transactions

    private val _detailTransaksi = MutableLiveData<DetailTransaksiResponse.DetailDataTransaction>()
    val detailTransaksi: LiveData<DetailTransaksiResponse.DetailDataTransaction> = _detailTransaksi

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getTransaction() {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getHistory()
        call.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(
                call: Call<TransactionResponse>,
                response: Response<TransactionResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    _transactions.value = response.body()?.data
                    _errorMessage.value = "Data Transaksi Ditemukan"
                    showLoading(false)
                } else {
                    _errorMessage.value = "Tidak ada data transaksi"
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                showLoading(false)
                _errorMessage.value = "Gagal memuat data: ${t.message}"
            }

        })
    }

    fun deleteTransaction(id_transaksi: String) {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val request = DeleteHistoryRequest(id_transaksi = id_transaksi)
        val call = retroInstance.deleteHistory(request)
        call.enqueue(object : Callback<DeleteHistoryResponse> {
            override fun onResponse(
                call: Call<DeleteHistoryResponse>,
                response: Response<DeleteHistoryResponse>
            ) {
                if (response.isSuccessful) {
                    showLoading(false)
                    _errorMessage.value = "Data Transaksi Pelanggan BERHASIL Di Hapus"
                    val newDataList = _transactions.value?.toMutableList()
                    newDataList?.removeAll { it.id_transaksi == id_transaksi }
                    getTransaction()
                } else {
                    showLoading(false)
                    _errorMessage.value = "Gagal menghapus data"
                }
                showLoading(false)
            }

            override fun onFailure(call: Call<DeleteHistoryResponse>, t: Throwable) {
                showLoading(false)
                _errorMessage.value = "Gagal menghapus data: ${t.message}"

            }
        })
    }

    fun putHistory(
        id_transaksi: String,
        status_bayar: String,
        status_barang: String,
        tgl_selesai: String
    ) {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val request = EditHistoryRequest(
            id_transaksi = id_transaksi,
            status_bayar = status_bayar,
            status_barang = status_barang,
            tgl_selesai = tgl_selesai
        )
        val call = retroInstance.editHistory(request)
        call.enqueue(object : Callback<EditHistoryResponse> {
            override fun onResponse(
                call: Call<EditHistoryResponse>,
                response: Response<EditHistoryResponse>
            ) {
                if (response.isSuccessful) {
                    showLoading(false)
                    _errorMessage.value = "Data Transaksi Pelanggan BERHASIL Di Ubah"
                    getTransaction()
                } else {
                    _errorMessage.value = "GAGAL mengubah data"
                }
            }

            override fun onFailure(call: Call<EditHistoryResponse>, t: Throwable) {
                showLoading(false)
                _errorMessage.value = "Gagal mengubah data: ${t.message}"
            }
        })
    }

    fun showLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}