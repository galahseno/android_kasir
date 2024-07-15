package id.dev.oxy

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.mazenrashed.printooth.Printooth
import dagger.hilt.android.HiltAndroidApp
import id.dev.oxy.data.local.DataStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class OxyApp : Application() {

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate() {
        super.onCreate()
        Printooth.init(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
    }

    private var lifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                CoroutineScope(Dispatchers.Main).launch {
                    dataStoreRepository.saveInputPinSession(true)
                }
            }
            else -> {}
        }
    }
}