package id.dev.oxy.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.data.model.auth.login.RegisterStatus
import id.dev.oxy.databinding.FragmentLoginBinding
import id.dev.oxy.ui.MainActivity
import id.dev.oxy.ui.auth.AuthViewModel
import id.dev.oxy.ui.compose.LoadingDialog
import id.dev.oxy.util.Resource
import id.dev.oxy.util.hideKeyboard

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginDialog.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }

        val username = binding.edtUsername
        val pin = binding.edtPin

        binding.btnMasuk.setOnClickListener {
            val usernameText = username.text?.trim()
            val pinText = pin.text?.trim()

            if (usernameText?.isEmpty() == true) {
                username.error = "Username harus diisi"
            }
            if (pinText?.isEmpty() == true) {
                pin.error = "Pin harus diisi"
            }
            if (usernameText?.isNotEmpty() == true && pinText?.isNotEmpty() == true) {
                authViewModel.handleLogin(usernameText.toString(), pinText.toString())
                binding.root.hideKeyboard()
            }

        }
        populateData()
    }

    private fun populateData() {
        authViewModel.loginResponse.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Idle -> {
                    binding.loginDialog.apply { setContent {} }
                }
                is Resource.Loading -> {
                    binding.loginDialog.apply { setContent { MaterialTheme { LoadingDialog() } } }
                }
                is Resource.Success -> {
                    authViewModel.run {
                        savePin(binding.edtPin.text.toString())
                        backToIdle()
                    }
                    if (resource.data.cashRegister == RegisterStatus.close.name) {
                        findNavController().navigate(R.id.action_loginFragment_to_openShiftFragment)
                    } else if (resource.data.cashRegister == RegisterStatus.open.name) {
                        Intent(requireActivity(), MainActivity::class.java).run {
                            startActivity(this)
                            requireActivity().finish()
                        }
                    }
                }
                is Resource.Error -> {
                    val unauthorized = resource.message == "Your credential does not match"

                    binding.loginDialog.apply {
                        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                        setContent {
                            MaterialTheme {
                                AlertDialog(
                                    title = {
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
                                                text = if (unauthorized) "Username atau PIN salah"
                                                else resource.message,
                                                fontSize = MaterialTheme.typography.subtitle1.fontSize,
                                                fontWeight = FontWeight.Normal,
                                            )
                                        }
                                    },
                                    confirmButton = { },
                                    onDismissRequest = { authViewModel.backToIdle() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}