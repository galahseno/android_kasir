package id.dev.oxy.ui.payment

import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.databinding.ActivityPaymentBinding
import id.dev.oxy.ui.MainActivity
import id.dev.oxy.ui.auth.AuthViewModel
import id.dev.oxy.ui.auth.opencash.InputPinDialogFragment

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        authViewModel.getInputPinSession.observe(this) {
            val fragmentManager = supportFragmentManager
            val pinDialog = InputPinDialogFragment()
            pinDialog.isCancelable = false
            pinDialog.dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            if (it) {
                pinDialog.show(fragmentManager, MainActivity::class.java.name)
            } else {
                pinDialog.dialog?.dismiss()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val keyguardManager: KeyguardManager =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (keyguardManager.isKeyguardLocked) {
            authViewModel.saveInputPinSession(true)
        }
    }
}