package com.example.cmcandroid.ui.portfolio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cmcandroid.R
import com.example.cmcandroid.data.api.CoinResponse
import com.example.cmcandroid.data.api.PortfolioEntryResponse
import java.text.NumberFormat
import java.util.Locale

class PortfolioAdapter(
    private val onDeleteClick: (PortfolioEntryResponse) -> Unit
) : ListAdapter<PortfolioEntryWithCoin, PortfolioAdapter.PortfolioViewHolder>(PortfolioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_portfolio_entry, parent, false)
        return PortfolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coinName: TextView = itemView.findViewById(R.id.coinName)
        private val coinTicker: TextView = itemView.findViewById(R.id.coinTicker)
        private val currentValue: TextView = itemView.findViewById(R.id.currentValue)
        private val quantity: TextView = itemView.findViewById(R.id.quantity)
        private val entryPrice: TextView = itemView.findViewById(R.id.entryPrice)
        private val profitLoss: TextView = itemView.findViewById(R.id.profitLoss)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(item: PortfolioEntryWithCoin) {
            val entry = item.entry
            val coin = item.coin
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
            val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
                minimumFractionDigits = 4
                maximumFractionDigits = 4
            }

            coinName.text = coin.name
            coinTicker.text = coin.ticker
            currentValue.text = currencyFormat.format(entry.quantity * coin.currentPrice)
            quantity.text = "Quantity: ${numberFormat.format(entry.quantity)}"
            entryPrice.text = "Entry Price: ${currencyFormat.format(entry.entryPrice)}"

            val profitLossValue = (entry.quantity * coin.currentPrice) - (entry.quantity * entry.entryPrice)
            val profitLossPercentage = if (entry.entryPrice > 0) {
                (profitLossValue / (entry.quantity * entry.entryPrice)) * 100
            } else {
                0.0
            }

            val profitLossText = StringBuilder()
            profitLossText.append("P/L: ")
            profitLossText.append(currencyFormat.format(profitLossValue))
            profitLossText.append(" (")
            profitLossText.append(if (profitLossPercentage >= 0) "+" else "")
            profitLossText.append(String.format("%.2f%%", profitLossPercentage))
            profitLossText.append(")")

            profitLoss.text = profitLossText.toString()
            profitLoss.setTextColor(
                itemView.context.getColor(
                    if (profitLossValue >= 0) R.color.profit_green else R.color.loss_red
                )
            )

            deleteButton.setOnClickListener {
                onDeleteClick(entry)
            }
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