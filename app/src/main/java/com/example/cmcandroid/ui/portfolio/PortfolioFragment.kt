package com.example.cmcandroid.ui.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cmcandroid.R
import com.example.cmcandroid.data.api.PortfolioEntryResponse
import com.example.cmcandroid.data.api.PortfolioSummaryResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PortfolioFragment : Fragment() {

    private val viewModel: PortfolioViewModel by viewModels()
    private lateinit var portfolioAdapter: PortfolioAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalInvestment: TextView
    private lateinit var currentValue: TextView
    private lateinit var profitLoss: TextView
    private lateinit var profitLossPercentage: TextView
    private lateinit var progressBar: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_portfolio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.portfolioRecyclerView)
        totalInvestment = view.findViewById(R.id.totalInvestment)
        currentValue = view.findViewById(R.id.currentValue)
        profitLoss = view.findViewById(R.id.profitLoss)
        profitLossPercentage = view.findViewById(R.id.profitLossPercentage)
        progressBar = view.findViewById(R.id.progressBar)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        portfolioAdapter = PortfolioAdapter { entry ->
            showDeleteConfirmationDialog(entry)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = portfolioAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.portfolioEntries.observe(viewLifecycleOwner) { entries ->
            portfolioAdapter.submitList(entries)
        }

        viewModel.portfolioSummary.observe(viewLifecycleOwner) { summary ->
            summary?.let {
                updatePortfolioSummary(it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePortfolioSummary(summary: PortfolioSummaryResponse) {
        totalInvestment.text = "$${String.format("%.2f", summary.totalInvestment)}"
        currentValue.text = "$${String.format("%.2f", summary.currentValue)}"
        profitLoss.text = "$${String.format("%.2f", summary.profitLoss)}"
        profitLossPercentage.text = "${String.format("%.2f", summary.profitLossPercentage)}%"

        // Set text colors based on profit/loss
        val profitLossColor = if (summary.profitLoss >= 0) {
            R.color.profit_green
        } else {
            R.color.loss_red
        }
        profitLoss.setTextColor(resources.getColor(profitLossColor))
        profitLossPercentage.setTextColor(resources.getColor(profitLossColor))
    }

    private fun showDeleteConfirmationDialog(entry: PortfolioEntryResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Portfolio Entry")
            .setMessage("Are you sure you want to delete this portfolio entry? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deletePortfolioEntry(entry)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
} 