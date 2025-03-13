package com.example.cmcandroid.ui.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmcandroid.data.api.CoinResponse
import com.example.cmcandroid.data.api.PortfolioEntryResponse
import com.example.cmcandroid.data.api.PortfolioSummaryResponse
import com.example.cmcandroid.data.repository.CmcRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repository: CmcRepository
) : ViewModel() {

    private val _portfolioSummary = MutableLiveData<PortfolioSummaryResponse>()
    val portfolioSummary: LiveData<PortfolioSummaryResponse> = _portfolioSummary

    private val _portfolioEntries = MutableLiveData<List<PortfolioEntryWithCoin>>()
    val portfolioEntries: LiveData<List<PortfolioEntryWithCoin>> = _portfolioEntries

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadPortfolioData()
    }

    fun loadPortfolioData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                loadPortfolioSummary()
                loadPortfolioEntries()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load portfolio data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadPortfolioSummary() {
        _portfolioSummary.value = repository.getPortfolioSummary()
    }

    private suspend fun loadPortfolioEntries() {
        val entries = repository.getPortfolioEntries()
        val coins = repository.getAllCoins()
        val coinMap = coins.associateBy { it.id }

        val entriesWithCoins = entries.mapNotNull { entry ->
            coinMap[entry.cryptoId]?.let { coin ->
                PortfolioEntryWithCoin(entry, coin)
            }
        }.sortedByDescending { it.entry.id }

        _portfolioEntries.value = entriesWithCoins
    }

    fun refreshPortfolio() {
        loadPortfolioData()
    }
} 