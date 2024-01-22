package com.fajar.pratamalaundry_admin.presentation.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityLoginBinding
import com.fajar.pratamalaundry_admin.databinding.DialogLoadingBinding
import com.fajar.pratamalaundry_admin.presentation.main.MainActivity
import com.fajar.pratamalaundry_admin.viewmodel.ViewModelFactory
import com.fajar.pratamalaundry_admin.model.result.Result
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setViewModel()
        _binding.btnLogin.setOnClickListener {
            loginAction()
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory.getInstance(this, dataStore)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun loginAction() {
        val usernameEditText = _binding.etUsername
        val passwordEditText = _binding.etPassword

        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        when {
            username.isEmpty() -> usernameEditText.error =
                resources.getString(R.string.empty_username)

            password.isEmpty() -> passwordEditText.error = resources.getString(R.string.empty_pass)
            username.isEmpty() && password.isEmpty() -> {
                usernameEditText.error = resources.getString(R.string.empty_username)
                passwordEditText.error = resources.getString(R.string.empty_pass)
            }

            else -> {
                val customBind = DialogLoadingBinding.inflate(layoutInflater)
                val loadingDialogBuilder = AlertDialog.Builder(this).apply {
                    setView(customBind.root)
                    setCancelable(false)
                }
                val loadingDialog = loadingDialogBuilder.create()
                lifecycleScope.launch {
                    val fcm = loginViewModel.getTokenFcm()
                    loginViewModel.loginAdmin(username, password, fcm)
                        .observe(this@LoginActivity) { result ->
                            when (result) {
                                is Result.Loading -> loadingDialog.show()
                                is Result.Success -> {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Login Berhasil",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadingDialog.dismiss()
                                    loginViewModel.saveUser(result.data)
                                    Log.d(TAG, "loginAction: ${result.data}")
                                    toMain()
                                }

                                is Result.Error -> {
                                    loadingDialog.dismiss()
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Username dan Password Tidak diketahui",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d(TAG, "loginAction: ${result.error}")
                                    errorAlert(result.error)
                                }
                            }
                        }
                }

            }
        }
    }


    private fun errorAlert(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Login Gagal")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun toMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}