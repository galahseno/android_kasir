package id.dev.oxy.ui.customer

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dev.oxy.data.MainRepositoryImpl
import id.dev.oxy.data.local.DataStoreRepository
import id.dev.oxy.data.model.customer.add.CustomerRequestBody
import id.dev.oxy.data.model.customer.add.response.AddCustomerResponse
import id.dev.oxy.data.model.customer.add.response.group.CustomerGroupResponse
import id.dev.oxy.data.model.customer.detail.CustomerDetailResponse
import id.dev.oxy.data.model.customer.response.Data
import id.dev.oxy.util.Constant
import id.dev.oxy.util.Resource
import id.dev.oxy.util.Response
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import id.dev.oxy.data.model.sales.discount.Data as discount

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val dataStoreRepository: DataStoreRepository
) :
    ViewModel() {

    private val _queryCustomerName = MutableLiveData("")
    private val _queryDiscountName = MutableLiveData("")
    private val _customerId = MutableLiveData<String>()

    private val _detailCustomer = MutableLiveData<Resource<CustomerDetailResponse>>(Resource.Idle)
    val detailCustomer: LiveData<Resource<CustomerDetailResponse>> = _detailCustomer

    private val _customerGroup = MutableLiveData<Resource<CustomerGroupResponse>>(Resource.Idle)
    val customerGroup: LiveData<Resource<CustomerGroupResponse>> = _customerGroup

    private val _addCustomerResponse = MutableLiveData<Resource<AddCustomerResponse>>(Resource.Idle)
    val addCustomerResponse: LiveData<Resource<AddCustomerResponse>> = _addCustomerResponse

    val allCustomerPaging: LiveData<PagingData<Data>> =
        _queryCustomerName.switchMap {
            mainRepositoryImpl.getAllCustomer(it).cachedIn(viewModelScope)
        }

    val allDiscountPaging: LiveData<PagingData<discount>> =
        _queryDiscountName.switchMap {
            mainRepositoryImpl.getAllDiscount(it).cachedIn(viewModelScope)
        }

    fun setQueryCustomer(query: String) {
        _queryCustomerName.value = query
    }

    fun setQueryDiscount(query: String) {
        _queryDiscountName.value = query
    }

    fun getDetailCustomer(id: String) {
        setCustomerId(id)
        viewModelScope.launch {
            _detailCustomer.value = Resource.Loading

            val token = dataStoreRepository.readToken.first()
            when (val detailCustomer =
                mainRepositoryImpl.getDetailCustomer(Constant.HEADER_TOKEN + token, id)) {
                is Response.Success -> _detailCustomer.value = Resource.Success(detailCustomer.data)
                is Response.Error -> _detailCustomer.value =
                    Resource.Error(detailCustomer.errorMessage)
            }
        }
    }

    fun getCustomerGroup() {
        viewModelScope.launch {
            val token = dataStoreRepository.readToken.first()
            _customerGroup.value = Resource.Loading

            when (val customerGroup =
                mainRepositoryImpl.getCustomerGroup(Constant.HEADER_TOKEN + token)) {
                is Response.Success -> _customerGroup.value = Resource.Success(customerGroup.data)
                is Response.Error -> _customerGroup.value =
                    Resource.Error(customerGroup.errorMessage)
            }
        }
    }

    fun retryGetCustomerDetail() {
        _customerId.value?.let { getDetailCustomer(it) }
    }

    private fun setCustomerId(id: String) {
        _customerId.value = id
    }

    fun postCustomer(customerRequestBody: CustomerRequestBody) {
        viewModelScope.launch {
            val token = dataStoreRepository.readToken.first()
            _addCustomerResponse.value = Resource.Loading

            when (val response =
                mainRepositoryImpl.postCustomer(
                    Constant.HEADER_TOKEN + token,
                    customerRequestBody
                )) {
                is Response.Success -> _addCustomerResponse.value = Resource.Success(response.data)
                is Response.Error -> _addCustomerResponse.value =
                    Resource.Error(response.errorMessage)
            }
        }
    }

    fun backToIdle() {
        _addCustomerResponse.value = Resource.Idle
    }

    fun expiredToken() {
        viewModelScope.launch {
            dataStoreRepository.saveLoginSession(false)
            mainRepositoryImpl.deleteAllCart()
        }
    }
}