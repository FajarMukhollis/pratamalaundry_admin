package com.fajar.pratamalaundry_admin.presentation.main


import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.fajar.pratamalaundry_admin.databinding.ActivityMainBinding
import androidx.appcompat.app.AlertDialog
import com.fajar.pratamalaundry_admin.presentation.product.CategoryActivity
import com.fajar.pratamalaundry_admin.presentation.product.ProductActivity
import com.fajar.pratamalaundry_admin.presentation.profile.ProfileActivity
import com.fajar.pratamalaundry_admin.presentation.recap.RecapActivity
import com.fajar.pratamalaundry_admin.presentation.rules.RulesActivity
import com.fajar.pratamalaundry_admin.presentation.transaction.TransactionActivity
import com.fajar.pratamalaundry_admin.viewmodel.ViewModelFactory
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM", "Failed to get FCM token", task.exception)
                return@OnCompleteListener
            }
            // Token perangkat
            val token = task.result
            Log.d("FCM", "FCM Token (Petugas): $token")

            lifecycleScope.launch {
                mainViewModel.saveTokenFcm(token)
            }
        })

        requestNotificationPermission()

        setViewModel()
        getName()

        if (intent.action == "KOTLIN_NOTIFICATION_CLICK" || intent.getBooleanExtra("FROM_NOTIFICATION", false)) {
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

    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(this, dataStore)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }
    private fun requestNotificationPermission() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Izin Notifikasi Dibutuhkan")
                .setMessage("Aplikasi ini memerlukan izin notifikasi untuk memberikan informasi terbaru.")
                .setPositiveButton("Izinkan") { _, _ ->
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    startActivity(intent)
                }
                .setNegativeButton("Tolak") { _, _ ->

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
