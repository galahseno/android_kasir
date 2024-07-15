package id.dev.oxy.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dev.oxy.data.MainRepositoryImpl
import id.dev.oxy.data.local.DataStoreRepository
import id.dev.oxy.data.model.history.adapter.HistoryRow
import id.dev.oxy.data.model.history.detail.HistoryDetailResponse
import id.dev.oxy.util.Constant
import id.dev.oxy.util.Resource
import id.dev.oxy.util.Response
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val dataStoreRepository: DataStoreRepository,
) :
    ViewModel() {

    private val _historyId = MutableLiveData<String>()

    private val _detailHistory = MutableLiveData<Resource<HistoryDetailResponse>>(Resource.Idle)
    val detailHistory: LiveData<Resource<HistoryDetailResponse>> = _detailHistory

    private val _loginData = MutableLiveData<List<String>>()
    val loginData: LiveData<List<String>> = _loginData

    init {
        viewModelScope.launch {
            dataStoreRepository.readHeaderData.collectLatest {
                _loginData.value = it
            }
        }
    }

    val allHistoryPaging: LiveData<PagingData<HistoryRow>> =
        mainRepositoryImpl.getHistorySell().cachedIn(viewModelScope)

    fun getDetailHistory(id: String) {
        setHistoryId(id)
        viewModelScope.launch {
            _detailHistory.value = Resource.Loading
            val token = dataStoreRepository.readToken.first()

            when (val detailHistory =
                mainRepositoryImpl.getDetailHistory(Constant.HEADER_TOKEN + token, id)) {
                is Response.Success -> _detailHistory.value = Resource.Success(detailHistory.data)
                is Response.Error -> _detailHistory.value =
                    Resource.Error(detailHistory.errorMessage)
            }
        }
    }

    fun retryGetHistoryDetail() {
        _historyId.value?.let { getDetailHistory(it) }
    }

    private fun setHistoryId(id: String) {
        _historyId.value = id
    }

    fun expiredToken() {
        viewModelScope.launch {
            dataStoreRepository.saveLoginSession(false)
            mainRepositoryImpl.deleteAllCart()
        }
    }
}