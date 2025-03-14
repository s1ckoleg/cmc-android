package com.example.cmcandroid.data.repository

import com.example.cmcandroid.data.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CmcRepository @Inject constructor(
    private val api: CmcApi
) {
    // Authentication
    suspend fun register(username: String, email: String, password: String): AuthResponse {
        return api.register(RegisterRequest(username, email, password))
    }

    suspend fun login(username: String, password: String): AuthResponse {
        return api.login(LoginRequest(username, password))
    }

    // Coin Data
    suspend fun getAllCoins(): List<CoinResponse> {
        return api.getAllCoins()
    }

    suspend fun getCoinsByDate(date: String): List<CoinResponse> {
        return api.getCoinsByDate(date)
    }

    suspend fun getCoinById(id: Int): CoinResponse {
        return api.getCoinById(id)
    }

    suspend fun getCoinByIdAndDate(id: Int, date: String): CoinResponse {
        return api.getCoinByIdAndDate(id, date)
    }

    suspend fun getCoinHistory(id: Int, fromDate: String, toDate: String): List<CoinHistoryResponse> {
        return api.getCoinHistory(id, fromDate, toDate).history
    }

    suspend fun getCoinByTicker(ticker: String): CoinResponse {
        return api.getCoinByTicker(ticker)
    }

    // Portfolio Management
    suspend fun getPortfolioSummary(): PortfolioSummaryResponse {
        return api.getPortfolioSummary()
    }

    suspend fun getPortfolioEntries(): List<PortfolioEntryResponse> {
        return api.getPortfolioEntries()
    }

    suspend fun getPortfolioEntry(id: Int): PortfolioEntryResponse {
        return api.getPortfolioEntry(id)
    }

    suspend fun createPortfolioEntry(
        cryptoId: Int,
        quantity: Double,
        entryPrice: Double,
        notes: String?
    ): PortfolioEntryResponse {
        return api.createPortfolioEntry(
            CreatePortfolioEntryRequest(cryptoId, quantity, entryPrice, notes)
        )
    }

    suspend fun updatePortfolioEntry(
        id: Int,
        quantity: Double,
        entryPrice: Double,
        notes: String?
    ): PortfolioEntryResponse {
        return api.updatePortfolioEntry(
            id,
            UpdatePortfolioEntryRequest(quantity, entryPrice, notes)
        )
    }

    suspend fun deletePortfolioEntry(id: Int) {
        val response = api.deletePortfolioEntry(id)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete portfolio entry: ${response.code()}")
        }
    }
} 