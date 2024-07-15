package id.dev.oxy.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.data.model.customer.response.Data
import id.dev.oxy.databinding.FragmentCustomerBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.ui.customer.adapter.CustomerAdapter
import id.dev.oxy.ui.sales.adapter.LoadingStateAdapter
import id.dev.oxy.util.Resource
import id.dev.oxy.util.getErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CustomerFragment : Fragment() {
    private var _binding: FragmentCustomerBinding? = null
    private val binding get() = _binding!!

    private val customerViewModel: CustomerViewModel by activityViewModels()

    @Inject
    lateinit var customerAdapter: CustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCustomer.apply {
            adapter = customerAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { customerAdapter.retry() }
            )
            addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            )
        }

        customerAdapter.setOnItemClickCallback(object : CustomerAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Data) {
                customerViewModel.getDetailCustomer(data.id.toString())
            }
        })

        lifecycleScope.launch {
            customerAdapter.loadStateFlow.collectLatest {
                binding.apply {
                    loadingState.apply {
                        if (it.refresh is LoadState.Error) {
                            val errorMessage =
                                (it.refresh as LoadState.Error).error.getErrorMessage()
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
                                it.append.endOfPaginationReached && customerAdapter.itemCount == 0)

                        if (displayEmptyMessage) {
                            errorMsg.text =
                                StringBuilder().append("Belum ada pelanggan untuk ditampilkan")
                        }

                        progressBar.isVisible = it.refresh is LoadState.Loading
                        retryButton.isVisible = it.refresh is LoadState.Error
                        errorMsg.isVisible = it.refresh is LoadState.Error || displayEmptyMessage
                        retryButton.setOnClickListener { customerAdapter.retry() }
                    }
                    rvCustomer.isVisible =
                        it.refresh !is LoadState.Loading && it.refresh !is LoadState.Error
                }
            }
        }

        customerViewModel.allCustomerPaging.observe(viewLifecycleOwner) {
            customerAdapter.submitData(lifecycle, it)
        }

        binding.swipeRefresh.apply {
            setOnRefreshListener {
                customerAdapter.refresh().run {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
            setColorSchemeResources(R.color.bg_login)
        }

        customerViewModel.addCustomerResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                val snackBar = Snackbar.make(
                    binding.root,
                    R.string.success_add_customer,
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
                customerAdapter.refresh()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}