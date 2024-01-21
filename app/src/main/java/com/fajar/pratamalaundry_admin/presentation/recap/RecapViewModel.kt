package com.fajar.pratamalaundry_admin.presentation.recap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.response.OneMonthResponse
import com.fajar.pratamalaundry_admin.model.response.OneWeeksResponse
import retrofit2.*

class RecapViewModel : ViewModel() {

    private val _recapOneMonth = MutableLiveData<List<OneMonthResponse.OneMonth>>()
    val recapOneMonth: LiveData<List<OneMonthResponse.OneMonth>> = _recapOneMonth

    private val _recapOneWeek: MutableLiveData<List<OneWeeksResponse.OneWeek>> = MutableLiveData()
    val recapOneWeek: LiveData<List<OneWeeksResponse.OneWeek>> = _recapOneWeek

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchRecapOneMonth() {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getOneMonths()
        call.enqueue(object : Callback<OneMonthResponse> {
            override fun onResponse(
                call: Call<OneMonthResponse>,
                response: Response<OneMonthResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (!data.isNullOrEmpty()) {
                        _recapOneMonth.value = data
                    }
                }
            }

            override fun onFailure(call: Call<OneMonthResponse>, t: Throwable) {
                showLoading(false)
            }
        })
    }

    fun fetchRecapOneWeek() {
        showLoading(true)
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getOneWeeks()
        call.enqueue(object : Callback<OneWeeksResponse> {
            override fun onResponse(
                call: Call<OneWeeksResponse>,
                response: Response<OneWeeksResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    _recapOneWeek.value = response.body()?.data
                }
            }

            override fun onFailure(call: Call<OneWeeksResponse>, t: Throwable) {
                showLoading(false)
            }
        })
    }

    private fun showLoading(state: Boolean) {
        _isLoading.value = state
    }
}
