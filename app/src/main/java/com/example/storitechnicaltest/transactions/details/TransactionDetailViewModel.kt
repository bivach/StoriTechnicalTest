package com.example.storitechnicaltest.transactions.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storitechnicaltest.core.domain.TransactionsRepository
import com.storitechnicaltest.core.domain.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<TransactionDetailUIState>(TransactionDetailUIState.Start)
    val uiState = _uiState.asStateFlow()

    fun getTransactionDetail(transactionId: String) {
        viewModelScope.launch {
            _uiState.emit(TransactionDetailUIState.Loading)
            val transaction = transactionsRepository.getTransactionById(transactionId)
            if (transaction != null) {
                _uiState.emit(TransactionDetailUIState.Success(transaction))
            } else {
                _uiState.emit(TransactionDetailUIState.Error("Transaction not found"))
            }
        }
    }

    sealed class TransactionDetailUIState {
        object Start : TransactionDetailUIState()
        object Loading : TransactionDetailUIState()
        data class Success(val transaction: Transaction) : TransactionDetailUIState()
        data class Error(val message: String) : TransactionDetailUIState()
    }
}
