package com.fajar.pratamalaundry_admin.presentation.recap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.pratamalaundry_admin.databinding.ActivityOneMonthBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.response.OneMonthResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.OneMonthAdapter
import retrofit2.*

class OneMonthActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityOneMonthBinding
    private lateinit var adapterRecap: OneMonthAdapter
    private lateinit var recapViewModel : RecapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOneMonthBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        recapViewModel = ViewModelProvider(this).get(RecapViewModel::class.java)
        recapViewModel.fetchRecapOneMonth()

        recapViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        observeOneMonthData()
        setActionBar()
        initRecyclerView()
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

    private fun showLoading(loading: Boolean) {
        _binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun observeOneMonthData() {
        recapViewModel.recapOneMonth.observe(this) { oneMonth ->
            adapterRecap.setData(oneMonth!!)

            val totalIncome = calculateTotalIncome(oneMonth)
            val totalIncomeText = "Rp. $totalIncome"
            _binding.tvTotalAmount.text = totalIncomeText
        }
    }

    private fun calculateTotalIncome(oneMonth: List<OneMonthResponse.OneMonth>?): Int {
        var totalIncome = 0
        oneMonth?.forEach { item ->
            val price = item.total_harga.toIntOrNull() ?: 0
            totalIncome += price
        }
        return totalIncome
    }

    private fun initRecyclerView(){
        val rvOneMonth = _binding.rvOneMonth
        adapterRecap = OneMonthAdapter()
        rvOneMonth.apply {
            layoutManager = LinearLayoutManager(this@OneMonthActivity)
            val decoration = DividerItemDecoration(this@OneMonthActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = adapterRecap
            setHasFixedSize(true)
        }
    }

}
