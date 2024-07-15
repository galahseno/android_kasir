package id.dev.oxy.ui.sales

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.data.model.sales.product.Data
import id.dev.oxy.databinding.FragmentSalesBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.ui.sales.adapter.LoadingStateAdapter
import id.dev.oxy.ui.sales.adapter.SalesAdapter
import id.dev.oxy.ui.sales.cart.CartViewModel
import id.dev.oxy.util.Resource
import id.dev.oxy.util.createChip
import id.dev.oxy.util.getErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!

    private val salesViewModel: SalesViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    @Inject
    lateinit var adapter: SalesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        salesViewModel.getCategory()

        binding.rvItemSales.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )

        adapter.setOnItemClickCallback(object : SalesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Data) {
                cartViewModel.addToCart(data)
            }
        })

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                binding.apply {
                    loadingState.apply {
                        if (it.refresh is LoadState.Error) {
                            val errorMessage = (it.refresh as LoadState.Error).error.getErrorMessage()
                            val expiredToken = errorMessage == "Unauthenticated."
                            if (expiredToken) {
                                Intent(requireActivity(), AuthActivity::class.java).run {
                                    startActivity(this)
                                    requireActivity().finish()
                                }
                                salesViewModel.expiredToken()
                            } else {
                                errorMsg.text = StringBuilder().append(errorMessage)
                            }
                        }
                        val displayEmptyMessage = (it.refresh is LoadState.NotLoading &&
                                it.append.endOfPaginationReached && adapter.itemCount == 0)

                        if (displayEmptyMessage) {
                            errorMsg.text =
                                StringBuilder().append("Produk yang dicari tidak ditemukan")
                        }

                        progressBar.isVisible = it.refresh is LoadState.Loading
                        retryButton.isVisible = it.refresh is LoadState.Error
                        errorMsg.isVisible = it.refresh is LoadState.Error || displayEmptyMessage
                        retryButton.setOnClickListener {
                            salesViewModel.getCategory()
                            adapter.retry()
                        }
                    }
                    rvItemSales.isVisible =
                        it.refresh !is LoadState.Loading && it.refresh !is LoadState.Error
                }
            }
        }

        salesViewModel.apply {
            allProductPaging.observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }

            categoryResponse.observe(viewLifecycleOwner) {
                binding.apply {
                    loadingState.apply {
                        when (it) {
                            is Resource.Idle -> {}
                            is Resource.Error -> {
                                val expiredToken = it.message == "Unauthenticated."
                                if (expiredToken) {
                                    Intent(requireActivity(), AuthActivity::class.java).run {
                                        startActivity(this)
                                        requireActivity().finish()
                                    }
                                    salesViewModel.expiredToken()
                                } else {
                                    errorMsg.text =
                                        StringBuilder().append(it.message)
                                }
                            }
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                it.data.data.sortedBy { it.name }.forEach { category ->
                                    binding.chipGroup.addView(
                                        createChip(
                                            requireContext(),
                                            category.id,
                                            category.name,
                                            onClickListener = {
                                                setSelectedCategoryId(it.id.toString())
                                            }
                                        )
                                    )
                                }
                            }
                        }
                        chipAll.isVisible = it !is Resource.Loading && it !is Resource.Error
                        progressBar.isVisible = it is Resource.Loading
                        errorMsg.isVisible = it is Resource.Error
                        retryButton.isVisible = it is Resource.Error
                        rvItemSales.isVisible =
                            it !is Resource.Loading && it !is Resource.Error
                    }
                }
            }
            binding.chipAll.setOnClickListener { setSelectedCategoryId("") }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}