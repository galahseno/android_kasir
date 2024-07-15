package id.dev.oxy.ui.sales.cart.component

import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.view.*
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
import id.dev.oxy.data.model.customer.response.Data
import id.dev.oxy.databinding.FragmentCustomerDialogBinding
import id.dev.oxy.ui.customer.CustomerViewModel
import id.dev.oxy.ui.customer.adapter.CustomerAdapter
import id.dev.oxy.ui.sales.adapter.LoadingStateAdapter
import id.dev.oxy.ui.sales.cart.CartViewModel
import id.dev.oxy.util.getErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CustomerDialogFragment : DialogFragment() {
    private var _binding: FragmentCustomerDialogBinding? = null
    private val binding get() = _binding!!

    private val customerViewModel: CustomerViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    @Inject
    lateinit var customerAdapter: CustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.NoActionBarDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCustomer.adapter = customerAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { customerAdapter.retry() }
        )
        binding.rvCustomer.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        customerAdapter.setOnItemClickCallback(object : CustomerAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Data) {
                cartViewModel.setCustomerOptions(data)
                dismiss()
            }
        })

        lifecycleScope.launch {
            customerAdapter.loadStateFlow.collectLatest {
                binding.apply {
                    loadingState.apply {
                        if (it.refresh is LoadState.Error) {
                            val errorMessage = (it.refresh as LoadState.Error).error.getErrorMessage()
                            errorMsg.text = StringBuilder().append(errorMessage)
                        }

                        val displayEmptyMessage = (it.refresh is LoadState.NotLoading &&
                                it.append.endOfPaginationReached && customerAdapter.itemCount == 0)

                        if (displayEmptyMessage) {
                            errorMsg.text =
                                StringBuilder().append("Pelanggan yang dicari tidak ditemukan")
                        }

                        progressBar.isVisible = it.refresh is LoadState.Loading
                        retryButton.isVisible = it.refresh is LoadState.Error
                        errorMsg.isVisible = it.refresh is LoadState.Error || displayEmptyMessage
                        retryButton.setOnClickListener { customerAdapter.retry() }
                    }
                    val visible = it.refresh !is LoadState.Loading && it.refresh !is LoadState.Error
                    rvCustomer.isVisible = visible
                    svCustomer.isVisible = visible
                }
            }
        }

        customerViewModel.allCustomerPaging.observe(viewLifecycleOwner) {
            customerAdapter.submitData(lifecycle, it)
        }

        binding.svCustomer.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        customerViewModel.setQueryCustomer(query)
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
                customerViewModel.setQueryCustomer("")
                setQuery("", false)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        customerViewModel.setQueryCustomer("")
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