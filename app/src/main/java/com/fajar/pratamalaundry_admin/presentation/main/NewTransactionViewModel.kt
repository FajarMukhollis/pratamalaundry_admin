package com.fajar.pratamalaundry_admin.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.fajar.pratamalaundry_admin.model.response.TransactionResponse

class NewTransactionViewModel(
    private val newTransaction: NewTransaction
) : ViewModel() {
    fun getTransactions(): LiveData<TransactionResponse> {
        return newTransaction.getHistory()
    }
}