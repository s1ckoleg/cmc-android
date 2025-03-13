package com.example.cmcandroid.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmcandroid.data.api.CoinResponse
import com.example.cmcandroid.data.repository.CmcRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CmcRepository
) : ViewModel() {

    private val _coins = MutableLiveData<List<CoinResponse>>()
    val coins: LiveData<List<CoinResponse>> = _coins

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadCoins()
    }

    fun loadCoins() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _coins.value = repository.getAllCoins()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load coins"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshCoins() {
        loadCoins()
    }
} 