package com.example.storitechnicaltest.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storitechnicaltest.core.domain.AuthRepository
import com.storitechnicaltest.core.domain.TransactionsRepository
import com.storitechnicaltest.core.model.Transaction
import com.storitechnicaltest.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<TransactionsUIState>(TransactionsUIState.Start)
    val uiState = _uiState.asStateFlow()

    init {
        getUserTransactions()
    }
    
    fun getUserTransactions() {
        viewModelScope.launch {
            _uiState.emit(TransactionsUIState.Loading)
            val user = authRepository.getUserData()
            val transactions = transactionsRepository.getUserTransactions()
            if (transactions.isNotEmpty() && user != null) {
                _uiState.emit(TransactionsUIState.Success(user, transactions))
            } else {
                _uiState.emit(TransactionsUIState.Error("Error"))
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    sealed class TransactionsUIState {
        object Start : TransactionsUIState()
        object Loading : TransactionsUIState()
        data class Success(val user: User, val transactions: List<Transaction>) : TransactionsUIState()
        data class Error(val message: String) : TransactionsUIState()
    }
}
