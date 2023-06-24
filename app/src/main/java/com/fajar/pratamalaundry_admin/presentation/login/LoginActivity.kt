package com.fajar.pratamalaundry_admin.presentation.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.databinding.ActivityLoginBinding
import com.fajar.pratamalaundry_admin.databinding.DialogLoadingBinding
import com.fajar.pratamalaundry_admin.presentation.main.MainActivity
import com.fajar.pratamalaundry_admin.viewmodel.ViewModelFactory
import com.fajar.pratamalaundry_admin.model.result.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loadingDialog: AlertDialog

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
            username.isEmpty() -> usernameEditText.error = resources.getString(R.string.empty_username)
            password.isEmpty() -> passwordEditText.error = resources.getString(R.string.empty_pass)
            username.isEmpty() && password.isEmpty() -> {
                usernameEditText.error = resources.getString(R.string.empty_username)
                passwordEditText.error = resources.getString(R.string.empty_pass)
            }


            else -> {
                // Tampilkan loading dialog
                val customBind = DialogLoadingBinding.inflate(layoutInflater)
                val loadingDialogBuilder = AlertDialog.Builder(this).apply {
                    setView(customBind.root)
                    setCancelable(false)
                }
                loadingDialog = loadingDialogBuilder.create()
                loadingDialog.show()

                loginViewModel.loginAdmin(username, password).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> loadingDialog.show()
                        is Result.Success -> {
                            loadingDialog.dismiss()
                            loginViewModel.saveUser(result.data)
//                            loginViewModel.saveToken(result.data.token)
                            loginViewModel.getToken().observe(this) { token ->
                                println("ini token :$token")
                            }
                            toMain()
                        }
                        is Result.Error -> {
                            loadingDialog.dismiss()
                            errorAlert()
                        }
                    }
                }
            }
        }
    }

    private fun errorAlert() {
        AlertDialog.Builder(this).apply {
            setTitle(resources.getString(R.string.failed_login))
            setMessage(resources.getString(R.string.not_found))
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