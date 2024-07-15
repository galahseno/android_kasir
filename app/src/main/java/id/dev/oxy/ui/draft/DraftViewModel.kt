package id.dev.oxy.ui.draft

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dev.oxy.data.MainRepositoryImpl
import id.dev.oxy.data.local.entity.DraftTable
import id.dev.oxy.util.Resource
import id.dev.oxy.util.Response
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DraftViewModel @Inject constructor(
    private val mainRepositoryImpl: MainRepositoryImpl
) : ViewModel() {

    private val _draftId = MutableLiveData<Int>()

    private val _savedDraft = MediatorLiveData<List<DraftTable>>()
    val savedDraft: LiveData<List<DraftTable>> = _savedDraft

    private val _detailDraft = MutableLiveData<Resource<DraftTable>>(Resource.Idle)
    val detailDraft: LiveData<Resource<DraftTable>> = _detailDraft

    init {
        _savedDraft.addSource(mainRepositoryImpl.getDrafts()) {
            _savedDraft.value = it
        }
    }

    fun getDetailDraft(id: Int) {
        getDraftId(id)
        viewModelScope.launch {
            _detailDraft.value = Resource.Loading

            when (val detailDraft =
                mainRepositoryImpl.getDraftsById(id)) {
                is Response.Success -> _detailDraft.value = Resource.Success(detailDraft.data)
                is Response.Error -> _detailDraft.value =
                    Resource.Error(detailDraft.errorMessage)
            }
        }
    }

    fun retryGetDraftDetail() {
        _draftId.value?.let { getDetailDraft(it) }
    }

    private fun getDraftId(id: Int) {
        _draftId.value = id
    }

    override fun onCleared() {
        super.onCleared()
        _savedDraft.removeSource(mainRepositoryImpl.getDrafts())
    }
}