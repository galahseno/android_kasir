package id.dev.oxy.ui.draft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.databinding.FragmentDraftDetailBinding
import id.dev.oxy.ui.draft.adapter.DraftDetailAdapter
import id.dev.oxy.ui.sales.cart.CartViewModel
import id.dev.oxy.util.Resource
import id.dev.oxy.util.toCurrencyFormat
import javax.inject.Inject

@AndroidEntryPoint
class DraftDetailFragment : Fragment() {
    private var _binding: FragmentDraftDetailBinding? = null
    private val binding get() = _binding!!

    private val draftViewModel: DraftViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    @Inject
    lateinit var draftDetailAdapter: DraftDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDraftDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDraftItem.adapter = draftDetailAdapter

        draftViewModel.detailDraft.observe(viewLifecycleOwner) { resource ->
            binding.apply {
                loadingState.apply {
                    when (resource) {
                        is Resource.Idle -> {
                            loadingState.errorMsg.text = StringBuilder().append("Pilih Draft")
                        }
                        is Resource.Loading -> {
                            loadingState.progressBar.bringToFront()
                        }
                        is Resource.Success -> {
                            txtTotalMember.text = resource.data.products.sumOf {
                                it.lineDiscountMember.toInt()
                            }.times(-1).toString()

                            txtTotalHarga.text = resource.data.products.sumOf {
                                it.lineTotal
                            }.toString().toCurrencyFormat()

                            draftDetailAdapter.submitList(resource.data.products)
                            btnTransaction.setOnClickListener {
                                cartViewModel.loadFromDraft(resource.data)
                                it.findNavController().navigate(R.id.nav_sales)
                            }
                        }
                        is Resource.Error -> {
                            errorMsg.text =
                                StringBuilder().append(resource.message)
                        }
                    }
                    progressBar.isVisible = resource is Resource.Loading
                    errorMsg.isVisible = resource is Resource.Idle || resource is Resource.Error
                    retryButton.isVisible = resource is Resource.Error

                    retryButton.setOnClickListener { draftViewModel.retryGetDraftDetail() }
                    cvDraftDetail.isVisible = resource is Resource.Success
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}