package id.dev.oxy.ui.auth.opencash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.databinding.FragmentOpenShiftBinding
import id.dev.oxy.ui.MainActivity
import id.dev.oxy.ui.auth.AuthViewModel
import id.dev.oxy.ui.compose.LoadingDialog
import id.dev.oxy.util.Resource
import id.dev.oxy.util.hideKeyboard


@AndroidEntryPoint
class OpenShiftFragment : Fragment() {
    private var _binding: FragmentOpenShiftBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpenShiftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.openCashDialog.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }

        val cash = binding.edtCash
        val locationName = binding.dropdownStoreMenu

        binding.btnOpenShift.setOnClickListener {
            val cashText = cash.text?.trim()
            val locationText = locationName.text?.trim()

            if (cashText?.isEmpty() == true) {
                cash.error = "Cash harus diisi"
            }
            if (locationText?.isEmpty() == true) {
                locationName.error = "Lokasi harus diisi"
            }

            if (cashText?.isNotEmpty() == true && locationText?.isNotEmpty() == true) {
                authViewModel.handleOpenCash(cashText.toString().toInt())
                binding.root.hideKeyboard()
            }
        }

        authViewModel.businessLocationResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val location = response.data.map { data ->
                    "${data.name} ( ${data.locationId} )"
                }

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line, location
                )

                binding.dropdownStoreMenu.apply {
                    setOnClickListener { binding.root.hideKeyboard() }
                    showSoftInputOnFocus = false
                    setAdapter(adapter)
                    setOnItemClickListener { _, _, i, _ ->
                        authViewModel.setBusinessId(response.data[i].id)
                    }
                }
            }
        }
        populateData()
    }

    private fun populateData() {
        authViewModel.openCashResponse.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Idle -> {
                    binding.openCashDialog.apply { setContent {} }
                }
                is Resource.Loading -> {
                    binding.openCashDialog.apply { setContent { MaterialTheme { LoadingDialog() } } }
                }
                is Resource.Success -> {
                    authViewModel.run {
                        saveInputPinSession(false)
                        backToIdle()
                    }
                    Intent(requireActivity(), MainActivity::class.java).run {
                        startActivity(this)
                        requireActivity().finish()
                    }
                }
                is Resource.Error -> {
                    binding.openCashDialog.apply {
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
                                                text = resource.message,
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