package com.example.cmcandroid.ui.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cmcandroid.databinding.FragmentPortfolioBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class PortfolioFragment : Fragment() {

    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PortfolioViewModel by viewModels()
    private lateinit var portfolioAdapter: PortfolioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        portfolioAdapter = PortfolioAdapter()

        binding.portfolioEntriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = portfolioAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.portfolioSummary.observe(viewLifecycleOwner) { summary ->
            binding.apply {
                totalInvestment.text = formatPrice(summary.totalInvestment)
                currentValue.text = formatPrice(summary.currentValue)
                profitLoss.text = formatPrice(summary.profitLoss)
                profitLossPercentage.text = formatPercentage(summary.profitLossPercentage)
            }
        }

        viewModel.portfolioEntries.observe(viewLifecycleOwner) { entries ->
            portfolioAdapter.submitList(entries)
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

    private fun formatPercentage(percentage: Double): String {
        val prefix = if (percentage >= 0) "+" else ""
        return "$prefix${String.format("%.2f", percentage)}%"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 