package com.example.storitechnicaltest.navigation

import androidx.lifecycle.ViewModel
import com.storitechnicaltest.core.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun getStartDestination() = if (authRepository.isUserLoggedIn()) "home" else "login"
}
