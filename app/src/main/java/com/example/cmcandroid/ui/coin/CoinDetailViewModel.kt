package com.example.cmcandroid.ui.coin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmcandroid.data.api.CoinHistoryResponse
import com.example.cmcandroid.data.api.CoinResponse
import com.example.cmcandroid.data.repository.CmcRepository
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val repository: CmcRepository
) : ViewModel() {

    private val _coin = MutableLiveData<CoinResponse>()
    val coin: LiveData<CoinResponse> = _coin

    private val _priceHistory = MutableLiveData<Pair<LineData, List<String>>>()
    val priceHistory: LiveData<Pair<LineData, List<String>>> = _priceHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadCoinData(coinId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _coin.value = repository.getCoinById(coinId)
                loadPriceHistory(coinId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load coin data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadPriceHistory(coinId: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endDate = calendar.time
        
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.time

        val apiFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val history = repository.getCoinHistory(
            coinId,
            apiFormatter.format(startDate),
            apiFormatter.format(endDate)
        )

        val entries = history.mapIndexed { index, data ->
            Entry(index.toFloat(), data.price.toFloat())
        }

        val dataSet = LineDataSet(entries, "Price").apply {
            color = android.graphics.Color.BLUE
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            lineWidth = 2f
            setDrawFilled(true)
            fillColor = android.graphics.Color.BLUE
            fillAlpha = 30
        }

        val lineData = LineData(dataSet)
        lineData.setValueFormatter(null)

        _priceHistory.postValue(Pair(lineData, history.map { it.date }))
    }

    fun addToPortfolio(quantity: Double, entryPrice: Double, notes: String? = null) {
        viewModelScope.launch {
            try {
                val cryptoId = coin.value?.id ?: return@launch
                repository.createPortfolioEntry(
                    cryptoId = cryptoId,
                    quantity = quantity,
                    entryPrice = entryPrice,
                    notes = notes
                )
                // Show success message
                _error.value = "Successfully added to portfolio"
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add to portfolio"
            }
        }
    }
} 