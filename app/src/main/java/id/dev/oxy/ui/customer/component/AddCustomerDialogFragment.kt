package id.dev.oxy.ui.customer.component

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import id.dev.oxy.R
import id.dev.oxy.data.model.customer.add.CustomerRequestBody
import id.dev.oxy.databinding.FragmentAddCustomerDialogBinding
import id.dev.oxy.ui.compose.LoadingDialog
import id.dev.oxy.ui.customer.CustomerViewModel
import id.dev.oxy.util.Resource
import id.dev.oxy.util.hideKeyboard


class AddCustomerDialogFragment : DialogFragment() {

    private var _binding: FragmentAddCustomerDialogBinding? = null
    private val binding get() = _binding!!

    private val customerViewModel: CustomerViewModel by activityViewModels()

    private var customerGroupId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.NoActionBarDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCustomerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerViewModel.customerGroup.observe(viewLifecycleOwner) { resources ->
            binding.apply {
                loadingState.apply {
                    when (resources) {
                        is Resource.Idle -> {}
                        is Resource.Loading -> {}
                        is Resource.Error -> {
                            errorMsg.text = resources.message
                        }
                        is Resource.Success -> {
                            val memberType = mutableListOf(
                                "Tidak ada"
                            )
                            resources.data.data.map { data ->
                                memberType.add(data.name)
                            }

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line, memberType
                            )

                            binding.dropdownStoreMenu.apply {
                                setOnClickListener { binding.root.hideKeyboard() }
                                showSoftInputOnFocus = false
                                setAdapter(adapter)
                                setOnItemClickListener { _, _, i, _ ->
                                    if (i > 0) {
                                        customerGroupId = resources.data.data[i - 1].id.toString()
                                    }
                                }
                            }
                        }
                    }
                    progressBar.isVisible = resources is Resource.Loading
                    errorMsg.isVisible = resources is Resource.Error
                    retryButton.isVisible = resources is Resource.Error
                    retryButton.setOnClickListener { customerViewModel.getCustomerGroup() }

                    txtCustomerGroup.isVisible = resources is Resource.Success
                    txtAwalan.isVisible = resources is Resource.Success
                    txtNamaDepan.isVisible = resources is Resource.Success
                    txtNamaTengah.isVisible = resources is Resource.Success
                    txtNamaBelakang.isVisible = resources is Resource.Success
                    txtAlamat1.isVisible = resources is Resource.Success
                    txtAlamat2.isVisible = resources is Resource.Success
                    txtEmail.isVisible = resources is Resource.Success
                    txtMobile.isVisible = resources is Resource.Success

                    dropdownCustomerGroup.isVisible = resources is Resource.Success

                    edtAwalan.isVisible = resources is Resource.Success
                    edtNamaDepan.isVisible = resources is Resource.Success
                    edtNamaTengah.isVisible = resources is Resource.Success
                    edtNamaBelakang.isVisible = resources is Resource.Success
                    edtAlamat1.isVisible = resources is Resource.Success
                    edtAlamat2.isVisible = resources is Resource.Success
                    edtEmail.isVisible = resources is Resource.Success
                    edtMobile.isVisible = resources is Resource.Success
                    btnSend.isVisible = resources is Resource.Success
                }
            }
        }
        handleClick()
    }

    private fun handleClick() {
        binding.apply {
            btnSend.setOnClickListener {
                val awalan = edtAwalan.text
                val namaDepan = edtNamaDepan.text
                val namaTengah = edtNamaTengah.text
                val namaBelakang = edtNamaBelakang.text
                val mobile = edtMobile.text
                val email = edtEmail.text
                val alamat1 = edtAlamat1.text
                val alamat2 = edtAlamat2.text

                if (namaDepan?.isEmpty() == true || mobile?.isEmpty() == true) {
                    if (namaDepan?.isEmpty() == true) edtNamaDepan.error = "Wajib di-isi"
                    if (mobile?.isEmpty() == true) edtMobile.error = "Wajib di-isi"
                } else {
                    val requestBody = CustomerRequestBody(
                        customerGroupId = customerGroupId,
                        prefix = awalan.toString(),
                        firstName = namaDepan.toString(),
                        middleName = namaTengah.toString(),
                        lastName = namaBelakang.toString(),
                        mobile = mobile.toString(),
                        email = email.toString(),
                        addressLine1 = alamat1.toString(),
                        addressLine2 = alamat2.toString()
                    )
                    customerViewModel.postCustomer(requestBody)
                }
            }
        }

        customerViewModel.addCustomerResponse.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Idle -> {
                    binding.addCustomerDialog.apply { setContent {} }
                }
                is Resource.Loading -> {
                    binding.addCustomerDialog.apply { setContent { MaterialTheme { LoadingDialog() } } }
                }
                is Resource.Success -> {
                    customerViewModel.backToIdle()
                    dialog?.dismiss()
                }
                is Resource.Error -> {
                    binding.addCustomerDialog.apply {
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
                                    onDismissRequest = { customerViewModel.backToIdle() }
                                )
                            }
                        }
                    }
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
            window.setLayout((size.x * 0.50).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        }
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}