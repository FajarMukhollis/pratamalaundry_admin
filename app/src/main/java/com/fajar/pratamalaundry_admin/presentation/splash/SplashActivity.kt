package com.fajar.pratamalaundry_admin.presentation.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fajar.pratamalaundry_admin.databinding.ActivitySplashBinding
import com.fajar.pratamalaundry_admin.presentation.login.LoginActivity
import com.fajar.pratamalaundry_admin.presentation.main.MainActivity
import com.fajar.pratamalaundry_admin.presentation.main.MainViewModel
import com.fajar.pratamalaundry_admin.viewmodel.ViewModelFactory
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySplashBinding
    private lateinit var splashViewModel: SplashViewModel
    private lateinit var mainViewModel: MainViewModel

    private var isLogin = false
    private var splashTime = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_binding.root)

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
                val tokenfcm = mainViewModel.getTokenFcm()
                Log.d("FCM", "FCM Token (Petugas) ViewModel: $tokenfcm")
            }
        })

        setViewModel()

        Handler(Looper.getMainLooper()).postDelayed({
            if (isLogin) toMain() else toLogin()
        }, splashTime)
    }

    private fun toLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(this, dataStore)
        splashViewModel = ViewModelProvider(this, factory)[SplashViewModel::class.java]
        splashViewModel.getIsLogin().observe(this) {
            isLogin = it

        }
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }
}