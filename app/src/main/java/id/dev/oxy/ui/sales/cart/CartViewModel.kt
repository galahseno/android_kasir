package id.dev.oxy.ui.sales.cart

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dev.oxy.data.MainRepositoryImpl
import id.dev.oxy.data.local.DataStoreRepository
import id.dev.oxy.data.local.entity.CustomerTable
import id.dev.oxy.data.local.entity.DraftTable
import id.dev.oxy.data.local.entity.ProductTable
import id.dev.oxy.data.model.sales.discount.Data
import id.dev.oxy.data.model.transaction.Payment
import id.dev.oxy.data.model.transaction.Sell
import id.dev.oxy.data.model.transaction.TransactionRequestBody
import id.dev.oxy.data.model.transaction.response.TransactionResponse
import id.dev.oxy.util.Constant
import id.dev.oxy.util.Resource
import id.dev.oxy.util.Response
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue
import id.dev.oxy.data.model.customer.response.Data as customer
import id.dev.oxy.data.model.sales.product.Data as product

@HiltViewModel
class CartViewModel @Inject constructor(
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _productOptions = MutableLiveData<ProductTable>()
    val productOptions: LiveData<ProductTable>
        get() = _productOptions

    private val _customerOptions = MediatorLiveData<CustomerTable?>()
    val customerOptions: LiveData<CustomerTable?> = _customerOptions

    private val _cartProduct = MediatorLiveData<List<ProductTable>>()
    val cartProduct: LiveData<List<ProductTable>> = _cartProduct

    private val _totalPayment = MediatorLiveData<String>()
    val totalPayment: LiveData<String> = _totalPayment

    private val _totalReturn = MutableLiveData("0")
    val totalReturn: LiveData<String> = _totalReturn

    private val _paymentMethod = MutableLiveData("Uang Pas")
    val paymentMethod: LiveData<String> = _paymentMethod

    private val _cartNumber = MutableLiveData("")
    private val _pembulatanAmount = MutableLiveData("0")

    private val _postSalesResponse = MutableLiveData<Resource<TransactionResponse>>(Resource.Idle)
    val postSalesResponse: LiveData<Resource<TransactionResponse>> = _postSalesResponse

    init {
        _customerOptions.addSource(mainRepositoryImpl.getCustomerCart()) {
            _customerOptions.value = it
        }
        _cartProduct.addSource(mainRepositoryImpl.getCartProduct()) {
            _cartProduct.value = it
        }
        _totalPayment.addSource(_cartProduct.map { product ->
            product.sumOf { it.lineTotal }.toString().substringBefore(".")
        }) {
            _totalPayment.value = it
        }
    }

    fun setCartNumber(number: String) {
        _cartNumber.value = number
    }

    fun setPaymentMethod(paymentMethod: String) {
        _paymentMethod.value = paymentMethod
    }

    fun actionDiscountCart(discount: String, onMinusTotal: () -> Unit) {
        _cartProduct.value?.let { product ->
            _totalPayment.value = product.sumOf { it.lineTotal }.toString().substringBefore(".")
        }

        val totalPaymentAfterDiscountCart =
            _totalPayment.value?.toLong()?.minus(discount.toLong())
        _pembulatanAmount.value = discount.toInt().absoluteValue.toString()

        if (totalPaymentAfterDiscountCart != null) {
            if (totalPaymentAfterDiscountCart >= 0) {
                _totalPayment.value = totalPaymentAfterDiscountCart.toString()
            } else {
                onMinusTotal()
            }
        }
    }

    fun resetTotalPayment() {
        _cartProduct.value?.let { product ->
            _totalPayment.value = product.sumOf { it.lineTotal }.toString().substringBefore(".")
        }
        _pembulatanAmount.value = "0"
    }

    fun postSalesCash(totalCash: String?, onErrorTotalCash: () -> Unit) {
        _totalPayment.value?.let { total ->
            totalCash?.let {
                if (it.toLong() < total.toLong()) onErrorTotalCash()
                else {
                    _totalReturn.value =
                        _totalPayment.value?.toLong()
                            ?.minus(totalCash.toLong())?.absoluteValue.toString()
                    postSell()
                }
            }
        }
    }

    fun postSell() {
        viewModelScope.launch {
            _paymentMethod.value?.let { payment ->
                val locationId = dataStoreRepository.readLocationId.first()
                val customerId = dataStoreRepository.readCustomerId.first()
                val paymentMethod = when (payment) {
                    "Card" -> payment.lowercase(Locale("in", "ID"))
                    "E-Wallet" -> payment.lowercase(Locale("in", "ID"))
                    else -> "cash"
                }

                _totalPayment.value?.let { total ->
                    _cartProduct.value?.let { product ->
                        val requestBody = TransactionRequestBody(
                            listOf(
                                Sell(
                                    locationId = locationId,
                                    contactId = customerId.toString(),
                                    products = product.map { it.toProduct() },
                                    payments = listOf(
                                        Payment(
                                            method = paymentMethod,
                                            amount = total,
                                            cardNumber = _cartNumber.value ?: "",
                                        )
                                    ),
                                    discountType = "fixed",
                                    discountAmount = _pembulatanAmount.value ?: "",
                                    finalTotal = total,
                                    isCreatedFromApi = 1,
                                    status = "final"
                                )
                            )
                        )
                        handlePostSales(requestBody)
                    }
                }
            }
        }
    }

    private fun handlePostSales(transactionRequestBody: TransactionRequestBody) {
        viewModelScope.launch {
            viewModelScope.launch {
                _postSalesResponse.value = Resource.Loading
                val token = dataStoreRepository.readToken.first()

                when (val response =
                    mainRepositoryImpl.postSell(
                        Constant.HEADER_TOKEN + token,
                        transactionRequestBody
                    )) {
                    is Response.Success -> {
                        _postSalesResponse.value = Resource.Success(response.data)
                    }
                    is Response.Error -> _postSalesResponse.value =
                        Resource.Error(response.errorMessage)
                }
            }
        }
    }

    fun setProductOptions(productTable: ProductTable) {
        _productOptions.value = productTable
    }

    fun addToCart(data: product) {
        viewModelScope.launch {
            _customerOptions.value?.discountAmount.let { discountMember ->
                val productCart = mainRepositoryImpl.getCartProductById(data.id.toString())
                if (productCart != null) {
                    val quantity = productCart.quantity.plus(1)

                    val totalPriceAfterDiscount =
                        productCart.getTotalPriceAfterDiscountWithQuantity(quantity)

                    if (discountMember != null) {
                        val percentMember =
                            discountMember.toDouble().absoluteValue.div(100)
                        val discountPercentMember =
                            totalPriceAfterDiscount.times(percentMember)

                        val lineTotal =
                            if (productCart.isDiscountMember)
                                totalPriceAfterDiscount.minus(discountPercentMember)
                            else totalPriceAfterDiscount
                        val unitPrice =
                            if (productCart.isDiscountMember) lineTotal.div(quantity)
                            else totalPriceAfterDiscount.div(quantity)
                        val lineDiscountMember =
                            if (productCart.isDiscountMember) discountPercentMember
                            else 0.0

                        val productTable = productCart.copy(
                            quantity = quantity,
                            lineDiscountMember = lineDiscountMember,
                            lineTotal = lineTotal,
                            unitPrice = unitPrice
                        )
                        mainRepositoryImpl.updateProduct(productTable)
                    } else {
                        val productTable = productCart.copy(
                            quantity = quantity,
                            lineTotal = totalPriceAfterDiscount,
                            unitPrice = totalPriceAfterDiscount.div(quantity)
                        )
                        mainRepositoryImpl.updateProduct(productTable)
                    }
                } else {
                    val totalPriceAfterDiscount = data.toProductTable().getTotalPriceAfterDiscount()

                    if (discountMember != null) {
                        val percentMember =
                            discountMember.toDouble().absoluteValue.div(100)
                        val discountPercentMember =
                            totalPriceAfterDiscount.times(percentMember)

                        val lineTotal = totalPriceAfterDiscount.minus(discountPercentMember)

                        val productTable = data.toProductTableWithDiscountMember(
                            lineDiscountMember = discountPercentMember,
                            lineTotal = lineTotal
                        )
                        mainRepositoryImpl.addToCart(productTable)
                    } else {
                        mainRepositoryImpl.addToCart(data.toProductTable())
                    }
                }
            }
        }
    }

    fun setDiscountProduct(data: Data) {
        viewModelScope.launch {
            _customerOptions.value?.discountAmount.let { discountMember ->
                _productOptions.value?.let { product ->
                    val totalPriceAfterDiscount = data.getTotalPriceAfterDiscount(product)

                    if (discountMember != null) {
                        val percentMember =
                            discountMember.toDouble().absoluteValue.div(100)
                        val discountPercentMember =
                            totalPriceAfterDiscount.times(percentMember)

                        val lineTotal =
                            if (product.isDiscountMember)
                                totalPriceAfterDiscount.minus(discountPercentMember)
                            else totalPriceAfterDiscount
                        val unitPrice =
                            if (product.isDiscountMember)
                                lineTotal.div(product.quantity)
                            else totalPriceAfterDiscount.div(product.quantity)
                        val lineDiscountMember =
                            if (product.isDiscountMember) discountPercentMember else 0.0

                        _productOptions.value = _productOptions.value?.copy(
                            lineDiscountMember = lineDiscountMember,
                            lineDiscountType = data.discountType,
                            lineDiscountAmount = data.discountAmount.toDouble(),
                            unitPrice = unitPrice,
                            lineTotal = lineTotal,
                            discountProduct = data.name
                        )
                    } else {
                        _productOptions.value = _productOptions.value?.copy(
                            lineDiscountMember = 0.0,
                            lineDiscountType = data.discountType,
                            lineDiscountAmount = data.discountAmount.toDouble(),
                            unitPrice = totalPriceAfterDiscount.div(product.quantity),
                            lineTotal = totalPriceAfterDiscount,
                            discountProduct = data.name
                        )
                    }
                }
            }
            mainRepositoryImpl.updateProduct(_productOptions.value)
        }
    }

    fun setCustomerOptions(customer: customer) {
        viewModelScope.launch {
            mainRepositoryImpl.addCustomerCart(customer.toCustomerTable())
            dataStoreRepository.saveCustomerId(customer.id)

            _cartProduct.value?.map { product ->
                val totalPriceAfterDiscount = product.getTotalPriceAfterDiscount()

                if (customer.customerGroup != null) {
                    val percent =
                        customer.customerGroup.amount.toDouble().absoluteValue.div(100)
                    val discountMember =
                        totalPriceAfterDiscount.times(percent)

                    val lineTotal = totalPriceAfterDiscount.minus(discountMember)
                    val unitPrice = lineTotal.div(product.quantity)

                    product.apply {
                        isDiscountMember = true
                        lineDiscountMember = discountMember
                        this.unitPrice = unitPrice
                        this.lineTotal = lineTotal
                    }
                } else {
                    product.apply {
                        isDiscountMember = false
                        lineDiscountMember = 0.0
                        this.unitPrice = totalPriceAfterDiscount.div(product.quantity)
                        this.lineTotal = totalPriceAfterDiscount
                    }
                }
            }.also {
                mainRepositoryImpl.updateProductCart(it)
            }
        }
    }

    fun updateDiscountMember(value: Boolean) {
        viewModelScope.launch {
            _customerOptions.value?.discountAmount.let { discountMember ->
                _productOptions.value?.let { product ->
                    val totalPriceAfterDiscount = product.getTotalPriceAfterDiscount()

                    if (discountMember != null) {
                        val percentMember =
                            discountMember.toDouble().absoluteValue.div(100)
                        val discountPercentMember =
                            totalPriceAfterDiscount.times(percentMember)

                        val lineTotal =
                            if (value) totalPriceAfterDiscount.minus(discountPercentMember)
                            else totalPriceAfterDiscount
                        val unitPrice =
                            if (value) lineTotal.div(product.quantity)
                            else totalPriceAfterDiscount.div(product.quantity)
                        val lineDiscountMember = if (value) discountPercentMember else 0.0

                        _productOptions.value = _productOptions.value?.copy(
                            isDiscountMember = value,
                            lineDiscountMember = lineDiscountMember,
                            unitPrice = unitPrice,
                            lineTotal = lineTotal
                        )
                    } else {
                        _productOptions.value = _productOptions.value?.copy(
                            isDiscountMember = value,
                            lineDiscountMember = 0.0,
                            unitPrice = totalPriceAfterDiscount.div(product.quantity),
                            lineTotal = totalPriceAfterDiscount
                        )
                    }
                }
            }
            mainRepositoryImpl.updateProduct(_productOptions.value)
        }
    }

    fun updatePlusQuantity() {
        viewModelScope.launch {
            _customerOptions.value?.discountAmount.let { discountMember ->
                _productOptions.value?.let { product ->
                    val quantity = product.quantity.plus(1)

                    val totalPriceAfterDiscount =
                        product.getTotalPriceAfterDiscountWithQuantity(quantity)

                    if (discountMember != null) {
                        val percentMember =
                            discountMember.toDouble().absoluteValue.div(100)
                        val discountPercentMember =
                            totalPriceAfterDiscount.times(percentMember)

                        val lineTotal =
                            if (product.isDiscountMember)
                                totalPriceAfterDiscount.minus(discountPercentMember)
                            else totalPriceAfterDiscount
                        val unitPrice =
                            if (product.isDiscountMember) lineTotal.div(quantity)
                            else totalPriceAfterDiscount.div(quantity)
                        val lineDiscountMember =
                            if (product.isDiscountMember) discountPercentMember
                            else 0.0

                        _productOptions.value = _productOptions.value?.copy(
                            quantity = quantity,
                            lineTotal = lineTotal,
                            unitPrice = unitPrice,
                            lineDiscountMember = lineDiscountMember
                        )
                    } else {
                        _productOptions.value = _productOptions.value?.copy(
                            quantity = quantity,
                            lineTotal = totalPriceAfterDiscount,
                            unitPrice = totalPriceAfterDiscount.div(quantity)
                        )
                    }
                }
            }
            mainRepositoryImpl.updateProduct(_productOptions.value)
        }
    }

    fun updateMinusQuantity(onDeleteProduct: () -> Unit) {
        viewModelScope.launch {
            _customerOptions.value?.discountAmount.let { discountMember ->
                _productOptions.value?.let { product ->
                    if (product.quantity > 1) {
                        val quantity = product.quantity.minus(1)

                        val totalPriceAfterDiscount =
                            product.getTotalPriceAfterDiscountWithQuantity(quantity)

                        if (discountMember != null) {
                            val percentMember =
                                discountMember.toDouble().absoluteValue.div(100)
                            val discountPercentMember =
                                totalPriceAfterDiscount.times(percentMember)

                            val lineTotal =
                                if (product.isDiscountMember)
                                    totalPriceAfterDiscount.minus(discountPercentMember)
                                else totalPriceAfterDiscount
                            val unitPrice =
                                if (product.isDiscountMember) lineTotal.div(quantity)
                                else totalPriceAfterDiscount.div(quantity)
                            val lineDiscountMember =
                                if (product.isDiscountMember) discountPercentMember
                                else 0.0

                            _productOptions.value = _productOptions.value?.copy(
                                quantity = quantity,
                                lineTotal = lineTotal,
                                unitPrice = unitPrice,
                                lineDiscountMember = lineDiscountMember
                            )
                        } else {
                            _productOptions.value = _productOptions.value?.copy(
                                quantity = quantity,
                                lineTotal = totalPriceAfterDiscount,
                                unitPrice = totalPriceAfterDiscount.div(quantity)
                            )
                        }
                        mainRepositoryImpl.updateProduct(_productOptions.value)
                    } else {
                        onDeleteProduct()
                        mainRepositoryImpl.deleteProduct(_productOptions.value)
                    }
                }
            }
        }
    }

    fun updateEditQuantity(quantity: Int, onDeleteProduct: () -> Unit) {
        viewModelScope.launch {
            if (quantity == 0) {
                onDeleteProduct()
                mainRepositoryImpl.deleteProduct(_productOptions.value)
            } else {
                _customerOptions.value?.discountAmount.let { discountMember ->
                    _productOptions.value?.let { product ->
                        val totalPriceAfterDiscount =
                            product.getTotalPriceAfterDiscountWithQuantity(quantity)

                        if (discountMember != null) {
                            val percentMember =
                                discountMember.toDouble().absoluteValue.div(100)
                            val discountPercentMember =
                                totalPriceAfterDiscount.times(percentMember)

                            val lineTotal =
                                if (product.isDiscountMember)
                                    totalPriceAfterDiscount.minus(discountPercentMember)
                                else totalPriceAfterDiscount
                            val unitPrice =
                                if (product.isDiscountMember) lineTotal.div(quantity)
                                else totalPriceAfterDiscount.div(quantity)
                            val lineDiscountMember =
                                if (product.isDiscountMember) discountPercentMember
                                else 0.0

                            _productOptions.value = _productOptions.value?.copy(
                                quantity = quantity,
                                lineTotal = lineTotal,
                                unitPrice = unitPrice,
                                lineDiscountMember = lineDiscountMember
                            )
                        } else {
                            _productOptions.value = _productOptions.value?.copy(
                                quantity = quantity,
                                lineTotal = totalPriceAfterDiscount,
                                unitPrice = totalPriceAfterDiscount.div(quantity)
                            )
                        }
                        mainRepositoryImpl.updateProduct(_productOptions.value)
                    }
                }
            }
        }
    }

    fun deleteDiscountProduct() {
        viewModelScope.launch {
            _customerOptions.value?.discountAmount.let { discountAmount ->
                _productOptions.value?.let { product ->
                    val totalPriceAfterDiscount =
                        product.unitPriceBeforeDiscount.times(product.quantity)

                    if (discountAmount != null) {
                        val percent = discountAmount.toDouble().absoluteValue.div(100)
                        val discountMember = totalPriceAfterDiscount.times(percent)

                        val lineTotal =
                            if (product.isDiscountMember)
                                totalPriceAfterDiscount.minus(discountMember)
                            else totalPriceAfterDiscount
                        val unitPrice =
                            if (product.isDiscountMember) lineTotal.div(product.quantity)
                            else totalPriceAfterDiscount.div(product.quantity)
                        val lineDiscountMember =
                            if (product.isDiscountMember) discountMember
                            else 0.0

                        _productOptions.value = _productOptions.value?.copy(
                            lineTotal = lineTotal,
                            lineDiscountMember = lineDiscountMember,
                            lineDiscountType = "",
                            lineDiscountAmount = 0.0,
                            discountProduct = null,
                            unitPrice = unitPrice
                        )
                    } else {
                        _productOptions.value = _productOptions.value?.copy(
                            lineTotal = totalPriceAfterDiscount,
                            lineDiscountType = "",
                            lineDiscountMember = 0.0,
                            lineDiscountAmount = 0.0,
                            discountProduct = null,
                            unitPrice = totalPriceAfterDiscount.div(product.quantity)
                        )
                    }
                    mainRepositoryImpl.updateProduct(_productOptions.value)
                }
            }
        }
    }

    fun saveToDraft() {
        viewModelScope.launch {
            _customerOptions.value.let { customer ->
                _cartProduct.value?.let { products ->
                    val customerId = dataStoreRepository.readCustomerId.first()
                    val draftTable = DraftTable(
                        customerId = customerId,
                        customerName = customer?.name ?: "Anonim",
                        customerDiscountAmount = customer?.discountAmount,
                        products = products
                    )
                    mainRepositoryImpl.insertDraft(draftTable).run {
                        mainRepositoryImpl.deleteAllCart()
                        dataStoreRepository.saveCustomerId(1)
                    }
                }
            }
        }
    }

    fun loadFromDraft(draftTable: DraftTable) {
        viewModelScope.launch {
            dataStoreRepository.saveCustomerId(draftTable.customerId)
            mainRepositoryImpl.apply {
                deleteAllCart()
                addCustomerCart(
                    CustomerTable(
                        name = draftTable.customerName,
                        discountAmount = draftTable.customerDiscountAmount
                    )
                )
                addListProductToCart(draftTable.products)
            }
        }
    }

    fun backToIdle() {
        _postSalesResponse.value = Resource.Idle
    }

    override fun onCleared() {
        super.onCleared()
        _customerOptions.removeSource(mainRepositoryImpl.getCustomerCart())
        _cartProduct.removeSource(mainRepositoryImpl.getCartProduct())
        _totalPayment.removeSource(_cartProduct.map { product ->
            product.sumOf { it.lineTotal }.toString().substringBefore(".")
        })
    }
}