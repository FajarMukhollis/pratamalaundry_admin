package com.fajar.pratamalaundry_admin.presentation.recap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityOneWeeksBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.response.OneMonthResponse
import com.fajar.pratamalaundry_admin.model.response.OneWeeksResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.OneMonthAdapter
import com.fajar.pratamalaundry_admin.presentation.adapter.OneWeekAdapter
import retrofit2.*

class OneWeeksActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityOneWeeksBinding
    private lateinit var adapterRecap: OneWeekAdapter
    private lateinit var recapViewModel: RecapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOneWeeksBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        recapViewModel = ViewModelProvider(this).get(RecapViewModel::class.java)

        recapViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        recapViewModel.fetchRecapOneWeek()
        observeOneWeekData()
        initRecyclerView()
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

    private fun showLoading(loading: Boolean) {
        _binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun observeOneWeekData() {
        recapViewModel.recapOneWeek.observe(this) { oneWeek ->
            adapterRecap.setData(oneWeek)

            val totalIncome = calculateTotalIncome(oneWeek)
            val totalIncomeText = "Rp. $totalIncome"
            _binding.tvTotalAmount.text = totalIncomeText
        }
    }

    private fun calculateTotalIncome(oneWeek: List<OneWeeksResponse.OneWeek>?): Int {
        var totalIncome = 0
        oneWeek?.forEach { item ->
            val price = item.total_harga.toIntOrNull() ?: 0
            totalIncome += price
        }
        return totalIncome
    }

    private fun showData(data: OneWeeksResponse) {
        val results = data.data
        adapterRecap.setData(results)
    }

    private fun initRecyclerView() {
        val rvOneWeek = _binding.rvOneWeek
        adapterRecap = OneWeekAdapter(arrayListOf())
        rvOneWeek.apply {
            layoutManager = LinearLayoutManager(this@OneWeeksActivity)
            val decoration = DividerItemDecoration(this@OneWeeksActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = adapterRecap
            setHasFixedSize(true)
        }

    }

}