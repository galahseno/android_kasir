package id.dev.oxy.ui.auth.opencash

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.mukesh.OTP_VIEW_TYPE_BORDER
import com.mukesh.OtpView
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.databinding.FragmentInputPinDialogBinding
import id.dev.oxy.ui.auth.AuthViewModel

@AndroidEntryPoint
class InputPinDialogFragment : DialogFragment() {
    private var _binding: FragmentInputPinDialogBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputPinDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.otpView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    var otpValue by remember { mutableStateOf("") }
                    var isPinFalse by remember { mutableStateOf(false) }
                    val pin by authViewModel.getPin.observeAsState()

                    OtpView(
                        otpText = otpValue,
                        onOtpTextChange = {
                            otpValue = it
                            if (otpValue.length == 6) {
                                if (otpValue == pin) {
                                    authViewModel.saveInputPinSession(false)
                                    dialog?.dismiss()
                                } else isPinFalse = true
                            } else isPinFalse = false
                        },
                        otpCount = 6,
                        type = OTP_VIEW_TYPE_BORDER,
                        password = true,
                        containerSize = 48.dp,
                        passwordChar = "•",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        charColor = Color.Black,
                        charBackground = if (isPinFalse) Color.Red.copy(alpha = 0.5f) else Color.Gray
                    )

                    binding.txtInputPin.text =
                        if (isPinFalse) stringResource(id = R.string.false_pin)
                        else stringResource(id = R.string.input_pin)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val windows = dialog.window
        windows?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))

        return dialog
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}