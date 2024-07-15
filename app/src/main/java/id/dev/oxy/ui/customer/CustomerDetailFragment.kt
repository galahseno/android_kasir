package id.dev.oxy.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.databinding.FragmentCustomerDetailBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.util.Resource

@AndroidEntryPoint
class CustomerDetailFragment : Fragment() {

    private var _binding: FragmentCustomerDetailBinding? = null
    private val binding get() = _binding!!

    private val customerViewModel: CustomerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerViewModel.detailCustomer.observe(viewLifecycleOwner) {
            binding.apply {
                loadingState.apply {
                    when (it) {
                        is Resource.Idle -> {
                            loadingState.errorMsg.text = StringBuilder().append("Pilih Pelanggan")
                        }
                        is Resource.Loading -> {
                            loadingState.progressBar.bringToFront()
                        }
                        is Resource.Success -> {
                            txtCustomerLevel.text = it.data.data[0].type
                            txtCustomerLevel.text = it.data.data[0].customerGroup ?: "-"
                            txtNamaCustomer.text = it.data.data[0].name
                            txtMobileCustomer.text = it.data.data[0].mobile
                            txtEmailCustomer.text = it.data.data[0].email ?: "-"
                            txtAlamatCustomer.text = buildString {
                                append(it.data.data[0].addressLine1 ?: "-")
                                append("\n")
                                append(it.data.data[0].addressLine2 ?: "-")
                            }
                        }
                        is Resource.Error -> {
                            val expiredToken = it.message == "Unauthenticated."

                            if (expiredToken) {
                                Intent(requireActivity(), AuthActivity::class.java).run {
                                    startActivity(this)
                                    requireActivity().finish()
                                }
                                customerViewModel.expiredToken()
                            } else {
                                errorMsg.text =
                                    StringBuilder().append(it.message)
                            }
                        }
                    }
                    progressBar.isVisible = it is Resource.Loading
                    errorMsg.isVisible = it is Resource.Idle || it is Resource.Error
                    retryButton.isVisible = it is Resource.Error

                    retryButton.setOnClickListener {
                        customerViewModel.retryGetCustomerDetail()
                    }
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