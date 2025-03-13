package com.example.cmcandroid.data.api

import retrofit2.http.*

interface CmcApi {
    // Authentication
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // Coin Data
    @GET("api/coins")
    suspend fun getAllCoins(): List<CoinResponse>

    @GET("api/coins/date/{date}")
    suspend fun getCoinsByDate(@Path("date") date: String): List<CoinResponse>

    @GET("api/coins/{id}")
    suspend fun getCoinById(@Path("id") id: Int): CoinResponse

    @GET("api/coins/{id}/date/{date}")
    suspend fun getCoinByIdAndDate(
        @Path("id") id: Int,
        @Path("date") date: String
    ): CoinResponse

    @GET("api/coins/{id}/history")
    suspend fun getCoinHistory(
        @Path("id") id: Int,
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): CoinHistoryWrapper

    @GET("api/coins/ticker/{ticker}")
    suspend fun getCoinByTicker(@Path("ticker") ticker: String): CoinResponse

    // Portfolio Management
    @GET("api/portfolio")
    suspend fun getPortfolioSummary(): PortfolioSummaryResponse

    @GET("api/portfolio/entries")
    suspend fun getPortfolioEntries(): List<PortfolioEntryResponse>

    @GET("api/portfolio/entries/{id}")
    suspend fun getPortfolioEntry(@Path("id") id: Int): PortfolioEntryResponse

    @POST("api/portfolio/entries")
    suspend fun createPortfolioEntry(@Body request: CreatePortfolioEntryRequest): PortfolioEntryResponse

    @PUT("api/portfolio/entries/{id}")
    suspend fun updatePortfolioEntry(
        @Path("id") id: Int,
        @Body request: UpdatePortfolioEntryRequest
    ): PortfolioEntryResponse

    @DELETE("api/portfolio/entries/{id}")
    suspend fun deletePortfolioEntry(@Path("id") id: Int)
}

// Request/Response data classes
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: Int,
    val username: String
)

data class CoinResponse(
    val id: Int,
    val name: String,
    val ticker: String,
    val currentPrice: Double,
    val marketCap: Double,
    val volume24h: Double
)

data class CoinHistoryResponse(
    val date: String,
    val price: Double,
    val volume: Double,
    val marketCap: Double
)

data class CoinHistoryWrapper(
    val history: List<CoinHistoryResponse>
)

data class PortfolioSummaryResponse(
    val totalInvestment: Double,
    val currentValue: Double,
    val profitLoss: Double,
    val profitLossPercentage: Double
)

data class PortfolioEntryResponse(
    val id: Int,
    val cryptoId: Int,
    val quantity: Double,
    val entryPrice: Double,
    val notes: String?
)

data class CreatePortfolioEntryRequest(
    val cryptoId: Int,
    val quantity: Double,
    val entryPrice: Double,
    val notes: String?
)

data class UpdatePortfolioEntryRequest(
    val quantity: Double,
    val entryPrice: Double,
    val notes: String?
) 