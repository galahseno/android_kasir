package id.dev.oxy.ui.sales.cart

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.data.local.entity.ProductTable
import id.dev.oxy.databinding.FragmentCartBinding
import id.dev.oxy.ui.MainActivity
import id.dev.oxy.ui.payment.PaymentActivity
import id.dev.oxy.ui.payment.PaymentFragment
import id.dev.oxy.ui.sales.SalesViewModel
import id.dev.oxy.ui.sales.adapter.CartAdapter
import id.dev.oxy.ui.sales.cart.component.CustomerDialogFragment
import id.dev.oxy.ui.sales.cart.component.ProductDialogFragment
import id.dev.oxy.util.Resource
import id.dev.oxy.util.hideKeyboard
import id.dev.oxy.util.toCurrencyFormat
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val salesViewModel: SalesViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    @Inject
    lateinit var adapter: CartAdapter

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == PaymentFragment.PRINT_RESULT_CODE) {
                val snackBar =
                    Snackbar.make(binding.root, R.string.printer_on_process, Snackbar.LENGTH_LONG)

                snackBar.setAction(R.string.action_done) {
                    snackBar.dismiss()
                }.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.login_btn))
                    .show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inMainActivity =
            requireActivity().javaClass.simpleName == MainActivity::class.java.simpleName
        binding.apply {
            btnPembayaran.isVisible = inMainActivity
            btnDraft.isVisible = inMainActivity
            actionDelete.isVisible = inMainActivity
            txtDiscount.isVisible = !inMainActivity
            edtDiscount.isVisible = !inMainActivity
        }

        handleClick()

        binding.rvCart.adapter = adapter
        binding.rvCart.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        cartViewModel.apply {
            customerOptions.observe(viewLifecycleOwner) {
                binding.tvCustomerName.text = it?.name ?: "Anonim"
            }

            cartProduct.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()

                binding.btnPembayaran.isEnabled = it.isNotEmpty()
                binding.btnDraft.isEnabled = it.isNotEmpty()
            }

            totalPayment.observe(viewLifecycleOwner) {
                binding.txtTotalPembayaran.text = it.toCurrencyFormat()
            }
        }

        adapter.setOnItemClickCallback(object : CartAdapter.OnItemClickCallback {
            override fun onItemClicked(productTable: ProductTable) {
                if (inMainActivity) {
                    cartViewModel.setProductOptions(productTable)
                    val productDialog = ProductDialogFragment()
                    productDialog.show(childFragmentManager, MainActivity::class.java.name)
                }
            }
        })
    }

    private fun handleClick() {
        val deleteCartDialog =
            AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.CartDialogStyle))

        deleteCartDialog.setCancelable(true)
            .setTitle(R.string.delete_cart)
            .setPositiveButton("Ya") { dialogInterface, _ ->
                salesViewModel.deleteAllProduct()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Tidak") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

        val addDraftDialog =
            AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.CartDialogStyle))

        addDraftDialog.setCancelable(true)
            .setTitle(R.string.save_draft)
            .setPositiveButton("Ya") { dialogInterface, _ ->
                cartViewModel.saveToDraft()
                dialogInterface.dismiss()
                val snackBar =
                    Snackbar.make(binding.root, R.string.save_draft_success, Snackbar.LENGTH_LONG)

                snackBar.setAction(R.string.action_done) {
                    snackBar.dismiss()
                }.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.login_btn))
                    .show()
            }
            .setNegativeButton("Tidak") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }


        binding.apply {
            val inMainActivity =
                requireActivity().javaClass.simpleName == MainActivity::class.java.simpleName

            btnDraft.setOnClickListener { addDraftDialog.show() }

            actionDelete.setOnClickListener { deleteCartDialog.show() }

            btnPembayaran.setOnClickListener {
                val intent =
                    Intent(requireActivity().applicationContext, PaymentActivity::class.java)
                requireActivity().overridePendingTransition(0, 0)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                resultLauncher.launch(intent)

            }

            cartToolbar.setNavigationOnClickListener { if (inMainActivity) handleSelectCustomer() }
            tvCustomerName.setOnClickListener { if (inMainActivity) handleSelectCustomer() }
            edtDiscount.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val txtRounded = edtDiscount.text?.trim()
                    if (txtRounded?.isNotEmpty() == true) {
                        cartViewModel.actionDiscountCart(txtRounded.toString(),
                            onMinusTotal = {
                                edtDiscount.setText("")
                                val snackBar = Snackbar.make(
                                    binding.root,
                                    R.string.error_diskon_value,
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
                            })
                    } else {
                        cartViewModel.resetTotalPayment()
                    }
                    root.hideKeyboard()
                    edtDiscount.clearFocus()
                }
                false
            }
        }

        cartViewModel.apply {
            totalPayment.observe(viewLifecycleOwner) { total ->
                binding.contentPaymentSuccess.txtTotal.text = total.toCurrencyFormat()
            }
            totalReturn.observe(viewLifecycleOwner) {
                binding.contentPaymentSuccess.txtKembalian.text = it.toCurrencyFormat()
            }

            postSalesResponse.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        binding.apply {
                            edtDiscount.apply {
                                setText("")
                                isVisible = false
                            }
                            cartToolbar.navigationIcon = null
                            tvCustomerName.apply {
                                text = getString(R.string.berhasil)
                                gravity = Gravity.CENTER
                            }
                            txtDiscount.isVisible = false
                            txtTotalPembayaran.isVisible = false
                            textPembayaran.isVisible = false
                            rvCart.isVisible = false

                            contentPaymentSuccess.root.isVisible = true

                            btnPenjualanBaru.apply {
                                isVisible = true
                                setOnClickListener {
                                    requireActivity().finish()
                                    salesViewModel.deleteAllProduct()
                                    cartToolbar.navigationIcon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_round_people_24
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleSelectCustomer() {
        val pinDialog = CustomerDialogFragment()
        pinDialog.show(childFragmentManager, MainActivity::class.java.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}