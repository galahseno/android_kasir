package id.dev.oxy.ui.printer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mazenrashed.printooth.Printooth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrinterViewModel @Inject constructor() : ViewModel() {

    private val _printConnected = MutableLiveData(false)
    val printConnected: LiveData<Boolean> = _printConnected

    init {
        _printConnected.value = Printooth.hasPairedPrinter()
    }

    fun setPrintConnect(value: Boolean) {
        _printConnected.value = value
    }
}