package id.dev.oxy.ui.sales.cart.component

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.data.model.sales.discount.Data
import id.dev.oxy.databinding.FragmentProductDialogBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.ui.customer.CustomerViewModel
import id.dev.oxy.ui.sales.adapter.DiscountAdapter
import id.dev.oxy.ui.sales.adapter.LoadingStateAdapter
import id.dev.oxy.ui.sales.cart.CartViewModel
import id.dev.oxy.util.getErrorMessage
import id.dev.oxy.util.hideKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDialogFragment : DialogFragment() {
    private var _binding: FragmentProductDialogBinding? = null
    private val binding get() = _binding!!

    private val customerViewModel: CustomerViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    @Inject
    lateinit var adapter: DiscountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.NoActionBarDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDiskon.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )

        binding.rvDiskon.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        adapter.setOnItemClickCallback(object : DiscountAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Data) {
                cartViewModel.setDiscountProduct(data)
                dialog?.dismiss()
            }
        })

        cartViewModel.productOptions.observe(viewLifecycleOwner) {
            binding.apply {
                txtJumlah.setText(it.quantity.toString())
                switchDiskon.isChecked = it.isDiscountMember
            }
        }

        customerViewModel.allDiscountPaging.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

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
                                customerViewModel.expiredToken()
                            } else {
                                errorMsg.text = StringBuilder().append(errorMessage)
                            }
                        }

                        val displayEmptyMessage = (it.refresh is LoadState.NotLoading &&
                                it.append.endOfPaginationReached && adapter.itemCount == 0)

                        if (displayEmptyMessage) {
                            errorMsg.text =
                                StringBuilder().append("Diskon yang dicari tidak ditemukan")
                        }

                        progressBar.isVisible = it.refresh is LoadState.Loading
                        retryButton.isVisible = it.refresh is LoadState.Error
                        errorMsg.isVisible = it.refresh is LoadState.Error || displayEmptyMessage
                        retryButton.setOnClickListener { adapter.retry() }
                    }

                    val visible = it.refresh !is LoadState.Loading && it.refresh !is LoadState.Error
                    rvDiskon.isVisible = visible
                    txt1.isVisible = visible
                    view2.isVisible = visible
                    txtJumlah.isVisible = visible
                    actionAdd.isVisible = visible
                    actionMinus.isVisible = visible
                    svDiscount.isVisible = visible
                    offButton.isVisible = visible

                    cartViewModel.customerOptions.observe(viewLifecycleOwner) { customer ->
                        binding.apply {
                            txtDiskonMember.isVisible =
                                (customer?.discountAmount != null) && it.refresh is LoadState.NotLoading
                            switchDiskon.isVisible =
                                (customer?.discountAmount != null) && it.refresh is LoadState.NotLoading
                            view1.isVisible =
                                (customer?.discountAmount != null) && it.refresh is LoadState.NotLoading
                        }
                    }
                }
            }
        }

        initView()
    }

    private fun initView() {
        binding.apply {
            actionMinus.setOnClickListener {
                cartViewModel.updateMinusQuantity(onDeleteProduct = {
                    dialog?.dismiss()
                })
            }

            actionAdd.setOnClickListener { cartViewModel.updatePlusQuantity() }

            txtJumlah.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (txtJumlah.text?.trim()?.isNotEmpty() == true) {
                        cartViewModel.updateEditQuantity(
                            txtJumlah.text.toString().toInt(),
                            onDeleteProduct = { dialog?.dismiss() })
                        root.hideKeyboard()
                        txtJumlah.clearFocus()
                    }
                    return@setOnEditorActionListener true
                }
                false
            }

            switchDiskon.setOnCheckedChangeListener { compoundButton, value ->
                if (compoundButton?.isPressed == true) cartViewModel.updateDiscountMember(value)
            }

            offButton.setOnClickListener {
                cartViewModel.deleteDiscountProduct()
                dialog?.dismiss()
            }

            svDiscount.apply {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            customerViewModel.setQueryDiscount(query)
                            clearFocus()
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })

                val closeButton = findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
                closeButton.setOnClickListener {
                    customerViewModel.setQueryDiscount("")
                    setQuery("", false)
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        customerViewModel.setQueryDiscount("")
    }

    override fun onResume() {
        val window = dialog?.window
        val size = Point()
        val display = window?.windowManager?.defaultDisplay
        display?.let {
            display.getSize(size)
            // Set the width of the dialog proportional of the screen width
            window.setLayout((size.x * 0.40).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.CENTER)
        }
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}