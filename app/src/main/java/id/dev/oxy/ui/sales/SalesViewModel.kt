package id.dev.oxy.ui.sales

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dev.oxy.data.MainRepositoryImpl
import id.dev.oxy.data.local.DataStoreRepository
import id.dev.oxy.data.model.category.CategoryResponse
import id.dev.oxy.data.model.sales.cash_register.CashRegisterCheckResponse
import id.dev.oxy.data.model.sales.product.Data
import id.dev.oxy.util.Constant
import id.dev.oxy.util.Resource
import id.dev.oxy.util.Response
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _queryName = MutableStateFlow("")
    private val _selectedCategoryId = MutableLiveData("")

    private val _categoryResponse = MutableLiveData<Resource<CategoryResponse>>(Resource.Idle)
    val categoryResponse: LiveData<Resource<CategoryResponse>> = _categoryResponse

    private val _cashRegisterResponse =
        MutableLiveData<Resource<CashRegisterCheckResponse>>(Resource.Idle)
    val cashRegisterResponse: LiveData<Resource<CashRegisterCheckResponse>> = _cashRegisterResponse

    private val _loginData = MutableLiveData<List<String>>()
    val loginData: LiveData<List<String>> = _loginData

    init {
        viewModelScope.launch {
            dataStoreRepository.readHeaderData.collectLatest {
                _loginData.value = it
            }
        }
    }

    fun setQuery(query: String) {
        _queryName.value = query
    }

    fun setSelectedCategoryId(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun getCategory() {
        viewModelScope.launch {
            _categoryResponse.value = Resource.Loading
            delay(100)
            val token = dataStoreRepository.readToken.first()
            when (val categoryResponse =
                mainRepositoryImpl.getCategory(Constant.HEADER_TOKEN + token)) {
                is Response.Success -> {
                    _categoryResponse.value = Resource.Success(categoryResponse.data)
                }
                is Response.Error -> _categoryResponse.value =
                    Resource.Error(categoryResponse.errorMessage)
            }
        }
    }

    fun checkCashRegister() {
        viewModelScope.launch {
            _cashRegisterResponse.value = Resource.Loading
            val token = dataStoreRepository.readToken.first()
            when (val response =
                mainRepositoryImpl.checkCashRegister(Constant.HEADER_TOKEN + token)) {
                is Response.Success -> {
                    _cashRegisterResponse.value = Resource.Success(response.data)
                }
                is Response.Error -> _cashRegisterResponse.value =
                    Resource.Error(response.errorMessage)
            }
        }
    }

    @OptIn(FlowPreview::class)
    val allProductPaging: LiveData<PagingData<Data>> =
        _queryName.debounce(300L).asLiveData().distinctUntilChanged().switchMap { query ->
            _selectedCategoryId.distinctUntilChanged().switchMap { categoryId ->
                mainRepositoryImpl.getAllProduct(query, categoryId).cachedIn(viewModelScope)
            }
        }

    fun deleteAllProduct() {
        viewModelScope.launch {
            mainRepositoryImpl.deleteAllCart()
            dataStoreRepository.saveCustomerId(1)
        }
    }

    fun expiredToken() {
        viewModelScope.launch {
            dataStoreRepository.saveLoginSession(false)
            mainRepositoryImpl.deleteAllCart()
        }
    }
}