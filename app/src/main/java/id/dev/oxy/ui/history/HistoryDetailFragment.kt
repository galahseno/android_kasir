package id.dev.oxy.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.databinding.FragmentHistoryDetailBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.ui.history.adapter.HistoryDetailAdapter
import id.dev.oxy.util.Resource
import id.dev.oxy.util.formatToDateDetail
import id.dev.oxy.util.toCurrencyFormat
import id.dev.oxy.util.toDecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class HistoryDetailFragment : Fragment() {
    private var _binding: FragmentHistoryDetailBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by activityViewModels()

    @Inject
    lateinit var historyDetailAdapter: HistoryDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPurchasedItem.adapter = historyDetailAdapter

        historyViewModel.detailHistory.observe(viewLifecycleOwner) {
            binding.apply {
                loadingState.apply {
                    when (it) {
                        is Resource.Idle -> {
                            loadingState.errorMsg.text = StringBuilder().append("Pilih Transaksi")
                        }
                        is Resource.Loading -> {
                            loadingState.progressBar.bringToFront()
                        }
                        is Resource.Success -> {
                            historyViewModel.loginData.observe(viewLifecycleOwner) { resource ->
                                txtCasierName.text = resource[0]
                            }

                            it.data.data[0].let { data ->
                                txtBranchName.text = buildString {
                                    append("OXY ")
                                    append(data.location.name)
                                }
                                txtBranchAddress.text = buildString {
                                    append(data.location.landmark ?: "")
                                }
                                txtPelangganName.text = data.contact.name

                                historyDetailAdapter.submitList(data.sellLines)

                                txtTotalMember.text = data.sellLines.sumOf {
                                    it.lineDiscountMember.substringBefore(".").toInt()
                                }.times(-1).toString().toDecimalFormat()
                                txtTotalTambahan.text =
                                    data.discountAmount.substringBefore(".").toInt().times(-1)
                                        .toString().toDecimalFormat()

                                txtTotalHarga.text = data.finalTotal.toCurrencyFormat()
                                txtJenisPembayaran.text =
                                    data.paymentLines[0].method.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase()
                                        else it.toString()
                                    }
                                txtTotalPembayaran.text =
                                    data.paymentLines[0].amount.toCurrencyFormat()

                                txtTanggal.text = data.transactionDate.formatToDateDetail()
                                txtIdTransaksi.text = buildString {
                                    append("#")
                                    append(data.invoiceNo)
                                }
                                txtStatusTransaksi.text = data.paymentStatus
                            }
                        }
                        is Resource.Error -> {
                            val expiredToken = it.message == "Unauthenticated."

                            if (expiredToken) {
                                Intent(requireActivity(), AuthActivity::class.java).run {
                                    startActivity(this)
                                    requireActivity().finish()
                                }
                                historyViewModel.expiredToken()
                            } else {
                                errorMsg.text =
                                    StringBuilder().append(it.message)
                            }
                        }
                    }
                    progressBar.isVisible = it is Resource.Loading
                    errorMsg.isVisible = it is Resource.Idle || it is Resource.Error
                    retryButton.isVisible = it is Resource.Error

                    retryButton.setOnClickListener { historyViewModel.retryGetHistoryDetail() }
                    cvHistory.isVisible = it is Resource.Success
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}