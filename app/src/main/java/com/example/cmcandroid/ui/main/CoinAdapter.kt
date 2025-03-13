package com.example.cmcandroid.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cmcandroid.data.api.CoinResponse
import com.example.cmcandroid.databinding.ItemCoinBinding
import java.text.NumberFormat
import java.util.Locale

class CoinAdapter(
    private val onCoinClick: (CoinResponse) -> Unit
) : ListAdapter<CoinResponse, CoinAdapter.CoinViewHolder>(CoinDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding = ItemCoinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CoinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CoinViewHolder(
        private val binding: ItemCoinBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCoinClick(getItem(position))
                }
            }
        }

        fun bind(coin: CoinResponse) {
            binding.apply {
                coinName.text = coin.name
                coinTicker.text = coin.ticker
                coinPrice.text = formatPrice(coin.currentPrice)
                marketCap.text = "Market Cap: ${formatMarketCap(coin.marketCap)}"
            }
        }

        private fun formatPrice(price: Double): String {
            return NumberFormat.getCurrencyInstance(Locale.US).format(price)
        }

        private fun formatPriceChange(change: Double): String {
            val prefix = if (change >= 0) "+" else ""
            return "$prefix${String.format("%.2f", change)}%"
        }

        private fun formatMarketCap(marketCap: Double): String {
            return when {
                marketCap >= 1e12 -> "$${String.format("%.2f", marketCap / 1e12)}T"
                marketCap >= 1e9 -> "$${String.format("%.2f", marketCap / 1e9)}B"
                marketCap >= 1e6 -> "$${String.format("%.2f", marketCap / 1e6)}M"
                else -> "$${String.format("%.2f", marketCap)}"
            }
        }
    }

    private class CoinDiffCallback : DiffUtil.ItemCallback<CoinResponse>() {
        override fun areItemsTheSame(oldItem: CoinResponse, newItem: CoinResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CoinResponse, newItem: CoinResponse): Boolean {
            return oldItem == newItem
        }
    }
} 