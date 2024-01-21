package com.fajar.pratamalaundry_admin.presentation.main


import android.app.NotificationManager
import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.fajar.pratamalaundry_admin.model.remote.ApiConfig
import com.fajar.pratamalaundry_admin.model.remote.ApiFirebase
import com.fajar.pratamalaundry_admin.model.request.Notification
import com.fajar.pratamalaundry_admin.model.request.NotificationRequest
import com.fajar.pratamalaundry_admin.model.response.NotificationResponse
import com.fajar.pratamalaundry_admin.presentation.MyWorker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val WORKER_TAG = "MyWorker"
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

        val workRequest = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            15, TimeUnit.MINUTES // Atur interval menjadi 3 menit
        ).addTag(WORKER_TAG).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORKER_TAG,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

        requestNotificationPermission()

        setViewModel()
        setViewModels()
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
                        val addedTransactions =
                            newTransactions.filter { it !in previousTransactions }

                        val pendingConfirmationTransactions =
                            addedTransactions.filter { it.status_barang == "Menunggu Konfirmasi" }

                        if (pendingConfirmationTransactions.isNotEmpty()) {
                            Log.d("NotificationDebug", "Notifikasi akan muncul")
                            // Memperbarui previousTransactions
                            previousTransactions = newTransactions.toList()

                            postNotification()
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

    private fun postNotification() {
        val retroInstance = ApiFirebase.getApiFirebase()
        lifecycleScope.launch {
            val serverKey =
                "AAAADRgdVUk:APA91bEWUU-SfVofYgWivzqc_971BtZXAnHEx9_aKPLAzMQiBa0ntRwlISevXQ-gg3vTKQoiIx61q7pDHNeaTPHmlRPvMIUnZJ58wF-v88SL6egdS3Qk9BKq2YWeIXxPJ24pjzruXsBs"
            val token = mainViewModel.getTokenFcm()
            val title = "Pesanan Baru"
            val content = "Ada pesanan baru yang harus anda konfirmasi."
            val reqNotification = NotificationRequest(
                to = token,
                notification = Notification(
                    title = title,
                    body = content
                )
            )
            Log.d(TAG, "serverKey: $serverKey")
            Log.d(TAG, "FCM Token Post (Petugas): $token")
            val call = retroInstance.sendNotification(
                "application/json",
                "key=$serverKey",
                reqNotification
            )

            call.enqueue(object : Callback<NotificationResponse> {
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()
                        Log.d(TAG, "serverKey: $serverKey")
                        Log.d(TAG, "FCM Token (Petugas): $token")

                    }
                }

                override fun onFailure(
                    call: Call<NotificationResponse>,
                    t: Throwable
                ) {
                    Log.d(TAG, "Fail serverKey: $serverKey")
                    Log.d(TAG, "Fail FCM Token (Petugas): $token")
                }

            })
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
