package com.fajar.pratamalaundry_admin.presentation.main


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
import android.util.Log
import android.widget.Toast
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.presentation.notification.NotificationIntentService
import com.fajar.pratamalaundry_admin.presentation.notification.PratamaLaundryFirebaseMessagingService
import com.google.firebase.FirebaseApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var newTransactionViewModel: NewTransactionViewModel
    private lateinit var _binding: ActivityMainBinding
    private var previousTransactions: List<TransactionResponse.Data> = emptyList()

    private val pollingHandler = Handler()
    private val pollingInterval = 10000 // Interval polling dalam milidetik (contoh: 1 menit)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        requestNotificationPermission()

        setViewModel()
        setViewModels()
        observeTransactions()
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

//        if (intent.action == "com.fajar.pratamalaundry_admin.NOTIFICATION") {
//            val title = intent.getStringExtra("title")
//            val body = intent.getStringExtra("body")
//            // Perform the necessary action based on the notification data
//            // For example, you can display a toast message
//            Toast.makeText(this, "$title\n$body", Toast.LENGTH_LONG).show()
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.app_name)
//            val descriptionText = getString(R.string.default_notification_channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel("default", name, importance).apply {
//                description = descriptionText
//            }
//
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }

    }

    override fun onResume() {
        super.onResume()
        startPolling()
    }

    override fun onPause() {
        super.onPause()
        stopPolling()
    }

    private fun startPolling() {
        pollingHandler.post(object : Runnable {
            override fun run() {
                getTransaction()
                pollingHandler.postDelayed(this, pollingInterval.toLong())
            }
        })
    }

    private fun stopPolling() {
        pollingHandler.removeCallbacksAndMessages(null)
    }

    private fun getTransaction() {
        val retroInstance = ApiConfig.getApiService()
        val call = retroInstance.getHistory()
        call.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(
                call: Call<TransactionResponse>,
                response: Response<TransactionResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val newTransactions = it.data
                        val addedTransactions = newTransactions.filter { it !in previousTransactions }

                        val pendingConfirmationTransactions =
                            addedTransactions.filter { it.status_barang == "Menunggu Konfirmasi" }

                        if (pendingConfirmationTransactions.isNotEmpty()) {
                            Log.d("NotificationDebug", "Notifikasi akan muncul")
                            // Pemanggilan enqueueWork di sini, atau di tempat lain yang sesuai
                            val notificationIntent = Intent(this@MainActivity, NotificationIntentService::class.java)
                            NotificationIntentService.enqueueWork(this@MainActivity, notificationIntent)

                            // Memperbarui previousTransactions
                            previousTransactions = newTransactions.toList()

                            // Logika lainnya
                        } else {
                            Log.d("NotificationDebug", "Tidak ada notifikasi yang akan muncul")
                        }
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Tidak ada pesanan baru",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
                // Bandingkan data transaksi baru dengan data transaksi sebelumnya
                val newTransactions = result.data
                val addedTransactions = newTransactions.filter { it !in previousTransactions }

                if (addedTransactions.isNotEmpty()) {
                    // Filter transaksi yang memiliki Status Barang "Menunggu Konfirmasi"
                    val pendingConfirmationTransactions =
                        addedTransactions.filter { it.status_barang == "Menunggu Konfirmasi" }

                    if (pendingConfirmationTransactions.isNotEmpty()) {
                        // Jika terdapat transaksi baru dengan Status Barang "Menunggu Konfirmasi", tampilkan notifikasi
                        showNotification()
                    }
                }

                // Simpan data transaksi sebelumnya untuk perbandingan berikutnya
                previousTransactions = newTransactions
            }
        }
    }

    // Tambahkan fungsi untuk menampilkan notifikasi
    private fun showNotification() {
        val notificationIntent = Intent(this, NotificationIntentService::class.java)
        NotificationIntentService.enqueueWork(this, notificationIntent)
        // Tambahan logika notifikasi lainnya jika diperlukan


    }


//    private fun observeTransactions() {
//        newTransactionViewModel.getTransactions().observe(this) { result ->
//            if (result != null && result.data.isNotEmpty()) {
//                val notificationIntent = Intent(this, PratamaLaundryFirebaseMessagingService::class.java)
//                startService(notificationIntent)
//            }
//        }
//    }

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

//    private fun startPolling() {
//        pollingHandler.post(object : Runnable {
//            override fun run() {
//                getTransaction()
//                pollingHandler.postDelayed(this, pollingInterval.toLong())
//            }
//        })
//    }
//
//    private fun stopPolling() {
//        pollingHandler.removeCallbacksAndMessages(null)
//    }

}
