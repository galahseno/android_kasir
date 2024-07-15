package id.dev.oxy.ui.sales

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import id.dev.oxy.R
import id.dev.oxy.databinding.FragmentCashRegisterInfoDialogBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.util.Resource
import id.dev.oxy.util.toCurrencyFormat

class CashRegisterInfoDialogFragment : DialogFragment() {
    private var _binding: FragmentCashRegisterInfoDialogBinding? = null
    private val binding get() = _binding!!

    private val salesViewModel: SalesViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.NoActionBarDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCashRegisterInfoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        salesViewModel.cashRegisterResponse.observe(viewLifecycleOwner) { resource ->
            binding.apply {
                loadingState.apply {
                    when (resource) {
                        is Resource.Idle -> {}
                        is Resource.Loading -> {
                            progressBar.bringToFront()
                        }
                        is Resource.Success -> {
                            txtCashRegisterAmount.text =
                                resource.data.cashRegister.substringBefore(".").toCurrencyFormat()
                            txtCashAmount.text =
                                resource.data.cash.substringBefore(".").toCurrencyFormat()
                            txtCardAmount.text =
                                resource.data.card.substringBefore(".").toCurrencyFormat()
                            txtEWalletAmount.text =
                                resource.data.eWallet.substringBefore(".").toCurrencyFormat()
                            txtTotalPaymentAmount.text =
                                resource.data.totalPayment.toString().toCurrencyFormat()
                            txtTotalCashAmount.text =
                                resource.data.totalCash.toString().toCurrencyFormat()
                        }
                        is Resource.Error -> {
                            val expiredToken = resource.message == "Unauthenticated."

                            if (expiredToken) {
                                Intent(requireActivity(), AuthActivity::class.java).run {
                                    startActivity(this)
                                    requireActivity().finish()
                                }
                                salesViewModel.expiredToken()
                            } else {
                                errorMsg.text = resource.message
                            }
                        }
                    }

                    progressBar.isVisible = resource is Resource.Loading
                    errorMsg.isVisible = resource is Resource.Error
                    retryButton.isVisible = resource is Resource.Error
                    retryButton.setOnClickListener { salesViewModel.checkCashRegister() }

                    txtCashRegisterInfo.isVisible = resource is Resource.Success
                    txtCashRegister.isVisible = resource is Resource.Success
                    txtCash.isVisible = resource is Resource.Success
                    txtCard.isVisible = resource is Resource.Success
                    txtEWallet.isVisible = resource is Resource.Success
                    txtTotalPayment.isVisible = resource is Resource.Success
                    txtTotalCash.isVisible = resource is Resource.Success

                    txtCashRegisterAmount.isVisible = resource is Resource.Success
                    txtCashAmount.isVisible = resource is Resource.Success
                    txtCardAmount.isVisible = resource is Resource.Success
                    txtEWalletAmount.isVisible = resource is Resource.Success
                    txtTotalPaymentAmount.isVisible = resource is Resource.Success
                    txtTotalCashAmount.isVisible = resource is Resource.Success
                }
            }
        }
    }

    override fun onResume() {
        val window = dialog?.window
        val size = Point()
        val display = window?.windowManager?.defaultDisplay
        display?.let {
            display.getSize(size)
            // Set the width of the dialog proportional of the screen width
            window.setLayout((size.x * 0.45).toInt(), (size.y * 0.60).toInt())
        }
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}