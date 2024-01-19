package com.fajar.pratamalaundry_admin.presentation.main


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.fajar.pratamalaundry_admin.databinding.ActivityMainBinding
import androidx.appcompat.app.AlertDialog
import com.fajar.pratamalaundry_admin.model.response.TransactionResponse
import com.fajar.pratamalaundry_admin.presentation.product.CategoryActivity
import com.fajar.pratamalaundry_admin.presentation.product.ProductActivity
import com.fajar.pratamalaundry_admin.presentation.profile.ProfileActivity
import com.fajar.pratamalaundry_admin.presentation.recap.RecapActivity
import com.fajar.pratamalaundry_admin.presentation.rules.RulesActivity
import com.fajar.pratamalaundry_admin.presentation.transaction.TransactionActivity
import com.fajar.pratamalaundry_admin.viewmodel.ViewModelFactory
import android.provider.Settings
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.presentation.notification.NotificationIntentService

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var newTransactionViewModel: NewTransactionViewModel
    private lateinit var _binding: ActivityMainBinding
    private var previousTransactions: List<TransactionResponse.Data> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        requestNotificationPermission()

        setViewModel()
        setViewModels()
//        observeTransactions()
        getName()

        if (intent.action == "OPEN_TRANSACTION_PAGE") {
            toTransaction()
        }
        _binding.profile.setOnClickListener {
            toProfile()
        }

        _binding.conTransaction.setOnClickListener {
            toTransaction()
        }

        _binding.conService.setOnClickListener {
            toService()
        }

        _binding.conRecap.setOnClickListener {
            toRecap()
        }

        _binding.conRules.setOnClickListener {
            toRules()
        }

        _binding.conCategory.setOnClickListener {
            toCategory()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.default_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("default", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(this, dataStore)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setViewModels() {
        val factory = ViewModelFactory.getInstance(this, dataStore)
        newTransactionViewModel =
            ViewModelProvider(this, factory)[NewTransactionViewModel::class.java]
    }

    private fun observeTransactions() {
        newTransactionViewModel.getTransactions().observe(this) { result ->
            if (result != null && result.data.isNotEmpty()) {
                // Pemanggilan enqueueWork di sini, atau di tempat lain yang sesuai
                val notificationIntent = Intent(this, NotificationIntentService::class.java)
                NotificationIntentService.enqueueWork(this, notificationIntent)

                // Logika lainnya
            }
        }
    }

    private fun requestNotificationPermission() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {
            // Menampilkan permintaan izin notifikasi
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Izin Notifikasi Dibutuhkan")
                .setMessage("Aplikasi ini memerlukan izin notifikasi untuk memberikan informasi terbaru.")
                .setPositiveButton("Izinkan") { _, _ ->
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    startActivity(intent)
                }
                .setNegativeButton("Tolak") { _, _ ->
                    // Handle jika pengguna menolak izin notifikasi
                }
                .show()
        }
    }

    private fun toService() {
        val moveToProduct = Intent(this, ProductActivity::class.java)
        startActivity(moveToProduct)
    }

    private fun getName() {
        mainViewModel.getNama().observe(this) { result ->
            if (result != null) {
                _binding.userName.text = result.username
            }
        }
    }

    private fun toProfile() {
        val movetoProfile = Intent(this, ProfileActivity::class.java)
        startActivity(movetoProfile)
    }

    private fun toTransaction() {
        val moveToTransaction = Intent(this, TransactionActivity::class.java)
        startActivity(moveToTransaction)
    }

    private fun toRecap() {
        val moveToRecap = Intent(this, RecapActivity::class.java)
        startActivity(moveToRecap)
    }

    private fun toRules() {
        val moveToRules = Intent(this, RulesActivity::class.java)
        startActivity(moveToRules)
    }

    private fun toCategory() {
        val moveToCategory = Intent(this, CategoryActivity::class.java)
        startActivity(moveToCategory)
    }
}
