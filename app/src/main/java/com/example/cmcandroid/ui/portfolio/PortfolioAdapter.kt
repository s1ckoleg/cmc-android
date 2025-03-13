package com.example.cmcandroid.ui.portfolio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cmcandroid.data.api.CoinResponse
import com.example.cmcandroid.data.api.PortfolioEntryResponse
import com.example.cmcandroid.databinding.ItemPortfolioEntryBinding
import java.text.NumberFormat
import java.util.Locale

class PortfolioAdapter : ListAdapter<PortfolioEntryWithCoin, PortfolioAdapter.PortfolioViewHolder>(PortfolioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val binding = ItemPortfolioEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PortfolioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PortfolioViewHolder(
        private val binding: ItemPortfolioEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PortfolioEntryWithCoin) {
            binding.coinName.setText(item.coin.name)
            binding.coinTicker.setText(item.coin.ticker)
            binding.currentValue.setText(formatPrice(item.coin.currentPrice * item.entry.quantity))
            binding.quantity.setText("Quantity: ${String.format("%.4f", item.entry.quantity)}")
            binding.entryPrice.setText("Entry Price: ${formatPrice(item.entry.entryPrice)}")

            val profitLoss = (item.coin.currentPrice - item.entry.entryPrice) * item.entry.quantity
            val profitLossPercentage = (item.coin.currentPrice - item.entry.entryPrice) / item.entry.entryPrice * 100
            val profitLossText = "P/L: ${formatPrice(profitLoss)} (${formatPercentage(profitLossPercentage)})"
            binding.profitLoss.setText(profitLossText)
        }

        private fun formatPrice(price: Double): String {
            return NumberFormat.getCurrencyInstance(Locale.US).format(price)
        }

        private fun formatPercentage(percentage: Double): String {
            val prefix = if (percentage >= 0) "+" else ""
            return "$prefix${String.format("%.2f", percentage)}%"
        }
    }

    private class PortfolioDiffCallback : DiffUtil.ItemCallback<PortfolioEntryWithCoin>() {
        override fun areItemsTheSame(oldItem: PortfolioEntryWithCoin, newItem: PortfolioEntryWithCoin): Boolean {
            return oldItem.entry.id == newItem.entry.id
        }

        override fun areContentsTheSame(oldItem: PortfolioEntryWithCoin, newItem: PortfolioEntryWithCoin): Boolean {
            return oldItem == newItem
        }
    }
}

data class PortfolioEntryWithCoin(
    val entry: PortfolioEntryResponse,
    val coin: CoinResponse
) 