package id.dev.oxy.ui.auth.logout

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
import id.dev.oxy.databinding.FragmentLogoutConfirmationBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.ui.auth.AuthViewModel
import id.dev.oxy.util.Resource

class LogoutConfirmationDialogFragment : DialogFragment() {
    private var _binding: FragmentLogoutConfirmationBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.NoActionBarDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogoutConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel.getCashAmount()

        binding.apply {
            btnNo.setOnClickListener { dialog?.dismiss() }

            btnYes.setOnClickListener {
                val closeAmount = edtCloseAmount.text
                val closingNote = edtClosingNote.text

                if (closeAmount?.isEmpty() == true) {
                    edtCloseAmount.error = "Wajib di-isi"
                } else {
                    authViewModel.handleLogout(closeAmount.toString(), closingNote.toString())
                }
            }
        }

        authViewModel.cashAmountResponse.observe(viewLifecycleOwner) { resource ->
            binding.apply {
                loadingState.apply {
                    when (resource) {
                        is Resource.Idle -> {}
                        is Resource.Loading -> {
                            progressBar.bringToFront()
                        }
                        is Resource.Success -> {
                            edtCloseAmount.setText(resource.data.totalCash.toString())
                        }
                        is Resource.Error -> {
                            val expiredToken = resource.message == "Unauthenticated."

                            if (expiredToken) {
                                Intent(requireActivity(), AuthActivity::class.java).run {
                                    startActivity(this)
                                    requireActivity().finish()
                                }
                                authViewModel.expiredToken()
                            } else {
                                errorMsg.text = resource.message
                            }
                        }
                    }

                    progressBar.isVisible = resource is Resource.Loading
                    errorMsg.isVisible = resource is Resource.Error
                    retryButton.isVisible = resource is Resource.Error
                    retryButton.setOnClickListener { authViewModel.getCashAmount() }

                    txtCloseAmount.isVisible = resource is Resource.Success
                    txtLogout.isVisible = resource is Resource.Success
                    txtClosingNote.isVisible = resource is Resource.Success
                    txtLogoutMessage.isVisible = resource is Resource.Success
                    edtCloseAmount.isVisible = resource is Resource.Success
                    edtClosingNote.isVisible = resource is Resource.Success
                    btnNo.isVisible = resource is Resource.Success
                    btnYes.isVisible = resource is Resource.Success
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
            window.setLayout((size.x * 0.35).toInt(), (size.y * 0.65).toInt())
        }
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}