package id.dev.oxy.ui.payment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.utilities.Printing
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.data.model.transaction.response.TransactionResponse
import id.dev.oxy.databinding.FragmentPaymentBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.ui.compose.LoadingDialog
import id.dev.oxy.ui.history.HistoryViewModel
import id.dev.oxy.ui.payment.adapter.PaymentSuccessAdapter
import id.dev.oxy.ui.sales.SalesViewModel
import id.dev.oxy.ui.sales.cart.CartViewModel
import id.dev.oxy.util.*
import javax.inject.Inject


@AndroidEntryPoint
class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by activityViewModels()
    private val salesViewModel: SalesViewModel by activityViewModels()
    private val historyViewModel: HistoryViewModel by activityViewModels()

    private var printing: Printing? = null

    @Inject
    lateinit var paymentSuccessAdapter: PaymentSuccessAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Printooth.hasPairedPrinter()) printing = Printooth.printer()

        handlePayment()

        binding.apply {
            toolbar.setNavigationOnClickListener {
                requireActivity().finish()
            }
            contentPayment.rgPembayaran.setOnCheckedChangeListener { radioGroup, _ ->
                radioGroup.checkedRadioButtonId.apply {
                    radioGroup.findViewById<RadioButton>(this).let {
                        val visible = it.text.toString() == getString(R.string.uang_pas)
                        if (visible) {
                            root.hideKeyboard()
                            cartViewModel.setCartNumber("")
                        }
                        cartViewModel.setPaymentMethod(it.text.toString())

                        contentPayment.txtMasukanJumlah.text = when (it.text) {
                            getString(R.string.uang_lebih) -> getString(R.string.masukan_jumlah)
                            else -> getString(R.string.masukan_nomer)
                        }
                        contentPayment.txtMasukanJumlah.isVisible = !visible
                        contentPayment.edtNumber.isVisible = !visible
                    }
                }
            }
        }
    }

    private fun handlePayment() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                cartViewModel.setCartNumber(p0.toString())
            }
        }

        cartViewModel.apply {
            totalPayment.observe(viewLifecycleOwner) { total ->
                binding.apply {
                    txtTotal.text = total.toCurrencyFormat()
                }
            }

            paymentMethod.observe(viewLifecycleOwner) { payment ->
                binding.apply {
                    val edtCash = contentPayment.edtNumber

                    when (payment) {
                        getString(R.string.uang_pas) -> {
                            btnBayar.apply {
                                setOnClickListener {
                                    cartViewModel.postSell()
                                }
                            }
                        }
                        getString(R.string.uang_lebih) -> {
                            edtCash.apply {
                                keyListener = DigitsKeyListener.getInstance("0123456789")
                                removeTextChangedListener(textWatcher)
                                setText("")
                                cartViewModel.setCartNumber("")
                            }

                            btnBayar.apply {
                                setOnClickListener {
                                    if (edtCash.text?.isEmpty() == true) {
                                        edtCash.error = "Wajib di-isi"
                                    } else {
                                        cartViewModel.postSalesCash(
                                            edtCash.text.toString(),
                                            onErrorTotalCash = {
                                                val snackBar = Snackbar.make(
                                                    this,
                                                    R.string.error_cash_value,
                                                    Snackbar.LENGTH_LONG
                                                )
                                                snackBar.setAction(R.string.action_done) {
                                                    snackBar.dismiss()
                                                }.setActionTextColor(
                                                    ContextCompat.getColor(
                                                        context, R.color.login_btn
                                                    )
                                                ).show()
                                            })
                                    }
                                }
                            }
                        }
                        getString(R.string.card) -> {
                            edtCash.apply {
                                keyListener =
                                    DigitsKeyListener.getInstance("0123456789.-")
                                removeTextChangedListener(textWatcher)
                                addTextChangedListener(textWatcher)
                                setText("")
                            }

                            btnBayar.apply {
                                setOnClickListener {
                                    if (edtCash.text?.isEmpty() == true) {
                                        edtCash.error = "Wajib di-isi"
                                        return@setOnClickListener
                                    } else {
                                        cartViewModel.postSell()
                                    }
                                }
                            }
                        }
                        getString(R.string.e_wallet) -> {
                            edtCash.apply {
                                keyListener =
                                    DigitsKeyListener.getInstance("0123456789+")
                                edtCash.removeTextChangedListener(textWatcher)
                                edtCash.addTextChangedListener(textWatcher)
                                setText("")
                            }

                            btnBayar.apply {
                                setOnClickListener {
                                    if (edtCash.text?.isEmpty() == true) {
                                        edtCash.error = "Wajib di-isi"
                                        return@setOnClickListener
                                    } else {
                                        cartViewModel.postSell()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        cartViewModel.postSalesResponse.observe(viewLifecycleOwner) { resource ->
            binding.apply {
                when (resource) {
                    is Resource.Idle -> {
                        postDialog.apply { setContent {} }
                    }
                    is Resource.Loading -> {
                        postDialog.apply { setContent { MaterialTheme { LoadingDialog() } } }
                    }
                    is Resource.Success -> {
                        cartViewModel.backToIdle().run {
                            btnBayar.isVisible = false
                            contentPayment.root.isVisible = false
                            txtTotal.isVisible = false
                            txtPembayaran.isVisible = false

                            actionPrint.isVisible = true
                            contentPaymentSuccess.root.isVisible = true

                            val snackBar = Snackbar.make(
                                binding.root, R.string.success_post_sales, Snackbar.LENGTH_LONG
                            )
                            snackBar.setAction(R.string.action_done) {
                                snackBar.dismiss()
                            }.setActionTextColor(
                                ContextCompat.getColor(
                                    requireContext(), R.color.login_btn
                                )
                            ).show()
                        }
                        toolbar.setNavigationOnClickListener {
                            requireActivity().finish()
                            salesViewModel.deleteAllProduct()
                        }

                        handlePrint(resource)
                        handleContentPaymentSuccess(resource.data)
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
                            handleError(resource.message)
                        }
                    }
                }
            }
        }
    }

    private fun handleContentPaymentSuccess(transactionResponse: TransactionResponse) {
        binding.contentPaymentSuccess.apply {
            rvPurchasedItem.adapter = paymentSuccessAdapter

            historyViewModel.loginData.observe(viewLifecycleOwner) { resource ->
                txtCasierName.text = resource[0]
            }

            transactionResponse.sell[0].let { data ->
                txtBranchName.text = buildString {
                    append("OXY ")
                    append(data.location.name)
                }
                txtBranchAddress.text = buildString {
                    append(data.location.landmark ?: "")
                }
                txtPelangganName.text = data.contact.name

                paymentSuccessAdapter.submitList(data.sellLines)

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

    }

    private fun handlePrint(resource: Resource.Success<TransactionResponse>) {
        binding.apply {
            actionPrint.setOnClickListener {
                if (!Printooth.hasPairedPrinter()) {
                    val snackBar = Snackbar.make(
                        binding.root,
                        R.string.printer_not_connected,
                        Snackbar.LENGTH_LONG
                    )

                    snackBar.setAction(R.string.action_done) {
                        snackBar.dismiss()
                    }.setActionTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.login_btn
                        )
                    ).show()
                } else {
                    historyViewModel.loginData.observe(viewLifecycleOwner) {
                        val cashierName = it[0]
                        val dataPrint = PrintSellHelper.formatPrint(
                            requireContext(),
                            resource.data.sell[0],
                            cashierName
                        )
                        printing?.print(dataPrint)

                        requireActivity().run {
                            setResult(PRINT_RESULT_CODE)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun handleError(message: String) {
        binding.postDialog.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    AlertDialog(title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Terjadi Error",
                                textAlign = TextAlign.Center,
                                fontSize = MaterialTheme.typography.h5.fontSize,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    },
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = message,
                                    fontSize = MaterialTheme.typography.subtitle1.fontSize,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                        },
                        confirmButton = { },
                        onDismissRequest = { cartViewModel.backToIdle() })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PRINT_RESULT_CODE = 909
    }
}