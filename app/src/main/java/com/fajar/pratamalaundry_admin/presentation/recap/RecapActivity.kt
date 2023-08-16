package com.fajar.pratamalaundry_admin.presentation.recap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityRecapBinding
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.response.OneWeeksResponse
import com.fajar.pratamalaundry_admin.presentation.adapter.OneWeekAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecapActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityRecapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRecapBinding.inflate(layoutInflater)
        setContentView(_binding.root)

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

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_week ->
                    if (checked) {
                        toOneWeeks()
                    }
                R.id.radion_month ->
                    if (checked) {
                        toOneMonth()
                    }
            }
        }
    }

    private fun toOneWeeks() {
        val toOneWeeks = Intent(this, OneWeeksActivity::class.java)
        startActivity(toOneWeeks)

    }

    private fun toOneMonth() {
        val toOneMonth = Intent(this, OneMonthActivity::class.java)
        startActivity(toOneMonth)
    }

}