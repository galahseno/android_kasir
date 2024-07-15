package id.dev.oxy.ui.auth

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dev.oxy.data.MainRepositoryImpl
import id.dev.oxy.data.local.DataStoreRepository
import id.dev.oxy.data.model.auth.login.LoginModel
import id.dev.oxy.data.model.auth.login.LoginResponse
import id.dev.oxy.data.model.auth.logout.LogoutRequestBody
import id.dev.oxy.data.model.auth.logout.LogoutResponse
import id.dev.oxy.data.model.auth.logout.cash_amount.CashAmountResponse
import id.dev.oxy.data.model.auth.opencash.OpenCashModel
import id.dev.oxy.data.model.auth.opencash.OpenCashResponse
import id.dev.oxy.data.model.business_location.BusinessLocationResponse
import id.dev.oxy.util.Constant.HEADER_TOKEN
import id.dev.oxy.util.Resource
import id.dev.oxy.util.Response
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _loginResponse = MutableLiveData<Resource<LoginResponse>>(Resource.Idle)
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    private val _businessLocationResponse = MutableLiveData<BusinessLocationResponse?>()
    val businessLocationResponse: LiveData<BusinessLocationResponse?>
        get() = _businessLocationResponse

    private val _openCashResponse = MutableLiveData<Resource<OpenCashResponse>>(Resource.Idle)
    val openCashResponse: LiveData<Resource<OpenCashResponse>>
        get() = _openCashResponse

    private val _cashAmountResponse = MutableLiveData<Resource<CashAmountResponse>>(Resource.Idle)
    val cashAmountResponse: LiveData<Resource<CashAmountResponse>>
        get() = _cashAmountResponse

    private val _logoutResponse = MutableLiveData<Resource<LogoutResponse>>(Resource.Idle)
    val logoutResponse: LiveData<Resource<LogoutResponse>>
        get() = _logoutResponse

    private val _businessId = MutableLiveData(-1)

    fun handleLogin(username: String, password: String) {
        val loginModel = LoginModel(username, password)

        viewModelScope.launch {
            _loginResponse.value = Resource.Loading
            when (val loginResponse = mainRepositoryImpl.handleLogin(loginModel)) {
                is Response.Success -> {
                    _loginResponse.value = Resource.Success(loginResponse.data)
                    dataStoreRepository.apply {
                        saveLoginResponse(loginResponse.data)
                        saveLoginSession(true)
                    }
                    getBusinessLocation(loginResponse.data.token)
                }
                is Response.Error -> _loginResponse.value =
                    Resource.Error(loginResponse.errorMessage)
            }
        }
    }

    private suspend fun getBusinessLocation(token: String) {
        when (val businessIdResponse =
            mainRepositoryImpl.getBusinessLocation(HEADER_TOKEN + token)) {
            is Response.Success -> {
                _businessLocationResponse.value = businessIdResponse.data
            }
            is Response.Error -> _businessLocationResponse.value = null
        }
    }

    fun setBusinessId(id: Int) {
        _businessId.value = id
    }

    fun handleOpenCash(initialAmount: Int) {
        val openCashModel = OpenCashModel(initialAmount, _businessId.value ?: -1)

        viewModelScope.launch {
            _openCashResponse.value = Resource.Loading
            val token = dataStoreRepository.readToken.first()

            when (val response =
                mainRepositoryImpl.handleOpenCash(HEADER_TOKEN + token, openCashModel)) {
                is Response.Success -> {
                    _openCashResponse.value = Resource.Success(response.data)
                    dataStoreRepository.saveLocationId(_businessId.value.toString())
                }
                is Response.Error -> _openCashResponse.value =
                    Resource.Error(response.errorMessage)
            }
        }
    }

    fun getCashAmount() {
        viewModelScope.launch {
            _cashAmountResponse.value = Resource.Loading
            val token = dataStoreRepository.readToken.first()

            when (val response = mainRepositoryImpl.getCashAmount(HEADER_TOKEN + token)) {
                is Response.Success -> {
                    _cashAmountResponse.value = Resource.Success(response.data)
                }
                is Response.Error -> _cashAmountResponse.value =
                    Resource.Error(response.errorMessage)
            }
        }
    }

    fun handleLogout(closingAmount: String, closingNote: String) {
        viewModelScope.launch {
            val requestBody = LogoutRequestBody(closingAmount, closingNote)

            _logoutResponse.value = Resource.Loading
            val token = dataStoreRepository.readToken.first()

            when (val logoutResponse =
                mainRepositoryImpl.handleLogout(HEADER_TOKEN + token, requestBody)) {
                is Response.Success -> {
                    _logoutResponse.value = Resource.Success(logoutResponse.data)
                    dataStoreRepository.saveLoginSession(false)
                    mainRepositoryImpl.apply {
                        deleteAllCart()
                        deleteDrafts()
                    }
                }
                is Response.Error -> _logoutResponse.value =
                    Resource.Error(logoutResponse.errorMessage)
            }
        }
    }

    fun savePin(pin: String) {
        viewModelScope.launch {
            dataStoreRepository.savePin(pin)
        }
    }

    fun saveInputPinSession(value: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.saveInputPinSession(value)
        }
    }

    val getLoginSession = dataStoreRepository.readIsLogin.asLiveData()

    val getPin = dataStoreRepository.readPin.asLiveData()

    val getInputPinSession = dataStoreRepository.readIsInputPin.asLiveData()

    fun backToIdle() {
        _loginResponse.value = Resource.Idle
        _openCashResponse.value = Resource.Idle
        _logoutResponse.value = Resource.Idle
    }

    fun expiredToken() {
        viewModelScope.launch {
            dataStoreRepository.saveLoginSession(false)
            mainRepositoryImpl.deleteAllCart()
        }
    }
}