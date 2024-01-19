package com.fajar.pratamalaundry_admin.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fajar.pratamalaundry_admin.model.remote.ApiService
import com.fajar.pratamalaundry_admin.model.response.TransactionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewTransaction(private val apiService : ApiService) {

    fun getHistory(): LiveData<TransactionResponse> {
        val result = MutableLiveData<TransactionResponse>()

        apiService.getHistory().enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    result.value = response.body()
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                // Handle failure
            }
        })

        return result
    }

}