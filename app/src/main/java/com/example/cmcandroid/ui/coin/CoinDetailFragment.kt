package com.example.cmcandroid.ui.coin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.cmcandroid.databinding.FragmentCoinDetailBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class CoinDetailFragment : Fragment() {

    private var _binding: FragmentCoinDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoinDetailViewModel by viewModels()
    private val args: CoinDetailFragmentArgs by navArgs()
    private lateinit var dateAxisFormatter: DateAxisValueFormatter

    private class DateAxisValueFormatter : IndexAxisValueFormatter() {
        private val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        private var dates = listOf<String>()

        fun updateDates(newDates: List<String>) {
            dates = newDates
        }

        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            if (index >= 0 && index < dates.size) {
                try {
                    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        .parse(dates[index])
                    return dateFormat.format(date!!)
                } catch (e: Exception) {
                    return ""
                }
            }
            return ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateAxisFormatter = DateAxisValueFormatter()
        setupChart()
        setupAddToPortfolioButton()
        observeViewModel()
        viewModel.loadCoinData(args.coinId)
    }

    private fun setupChart() {
        binding.priceChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(false)
            setDrawGridBackground(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                labelRotationAngle = 45f
                setLabelCount(5, false)
                valueFormatter = dateAxisFormatter
            }

            axisLeft.apply {
                setDrawGridLines(false)
                setPosition(com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            }
            axisRight.isEnabled = false
            legend.isEnabled = false

            // Set minimum height to accommodate rotated labels
            minimumHeight = (resources.displayMetrics.density * 300).toInt()
        }
    }

    private fun setupAddToPortfolioButton() {
        binding.addToPortfolioButton.setOnClickListener {
            showAddToPortfolioDialog()
        }
    }

    private fun showAddToPortfolioDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.example.cmcandroid.R.layout.dialog_add_to_portfolio, null)

        val quantityLayout = dialogView.findViewById<TextInputLayout>(com.example.cmcandroid.R.id.quantityInputLayout)
        val quantityInput = dialogView.findViewById<TextInputEditText>(com.example.cmcandroid.R.id.quantityInput)
        val priceLayout = dialogView.findViewById<TextInputLayout>(com.example.cmcandroid.R.id.priceInputLayout)
        val priceInput = dialogView.findViewById<TextInputEditText>(com.example.cmcandroid.R.id.priceInput)
        val notesInput = dialogView.findViewById<TextInputEditText>(com.example.cmcandroid.R.id.notesInput)

        // Pre-fill the price with current price
        viewModel.coin.value?.let { coin ->
            priceInput.setText(String.format("%.2f", coin.currentPrice))
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add to Portfolio")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val quantity = quantityInput.text?.toString()?.toDoubleOrNull()
                val price = priceInput.text?.toString()?.toDoubleOrNull()
                val notes = notesInput.text?.toString()?.takeIf { it.isNotBlank() }

                if (quantity == null || quantity <= 0) {
                    quantityLayout.error = "Please enter a valid quantity"
                    return@setPositiveButton
                }

                if (price == null || price <= 0) {
                    priceLayout.error = "Please enter a valid price"
                    return@setPositiveButton
                }

                viewModel.addToPortfolio(quantity, price, notes)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.coin.observe(viewLifecycleOwner) { coin ->
            binding.apply {
                coinName.text = coin.name
                coinTicker.text = coin.ticker
                coinPrice.text = formatPrice(coin.currentPrice)
                marketCap.text = formatMarketCap(coin.marketCap)
                volume24h.text = formatVolume(coin.volume24h)
            }
        }

        viewModel.priceHistory.observe(viewLifecycleOwner) { (lineData, dates) ->
            dateAxisFormatter.updateDates(dates)
            binding.priceChart.data = lineData
            binding.priceChart.invalidate()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
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

    private fun formatVolume(volume: Double): String {
        return when {
            volume >= 1e12 -> "$${String.format("%.2f", volume / 1e12)}T"
            volume >= 1e9 -> "$${String.format("%.2f", volume / 1e9)}B"
            volume >= 1e6 -> "$${String.format("%.2f", volume / 1e6)}M"
            else -> "$${String.format("%.2f", volume)}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 