package id.dev.oxy.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import id.dev.oxy.data.local.entity.CustomerTable
import id.dev.oxy.data.local.entity.DraftTable
import id.dev.oxy.data.local.entity.ProductTable
import id.dev.oxy.data.model.auth.login.LoginModel
import id.dev.oxy.data.model.auth.login.LoginResponse
import id.dev.oxy.data.model.auth.logout.LogoutRequestBody
import id.dev.oxy.data.model.auth.logout.LogoutResponse
import id.dev.oxy.data.model.auth.logout.cash_amount.CashAmountResponse
import id.dev.oxy.data.model.auth.opencash.OpenCashModel
import id.dev.oxy.data.model.auth.opencash.OpenCashResponse
import id.dev.oxy.data.model.business_location.BusinessLocationResponse
import id.dev.oxy.data.model.category.CategoryResponse
import id.dev.oxy.data.model.customer.add.CustomerRequestBody
import id.dev.oxy.data.model.customer.add.response.AddCustomerResponse
import id.dev.oxy.data.model.customer.add.response.group.CustomerGroupResponse
import id.dev.oxy.data.model.customer.detail.CustomerDetailResponse
import id.dev.oxy.data.model.history.adapter.HistoryRow
import id.dev.oxy.data.model.history.detail.HistoryDetailResponse
import id.dev.oxy.data.model.sales.cash_register.CashRegisterCheckResponse
import id.dev.oxy.data.model.sales.product.Data
import id.dev.oxy.data.model.transaction.TransactionRequestBody
import id.dev.oxy.data.model.transaction.response.TransactionResponse
import id.dev.oxy.util.Response
import id.dev.oxy.data.model.customer.response.Data as customer
import id.dev.oxy.data.model.sales.discount.Data as discount

interface MainRepository {

    suspend fun handleLogin(loginModel: LoginModel): Response<LoginResponse>
    suspend fun handleOpenCash(
        token: String,
        openCashModel: OpenCashModel
    ): Response<OpenCashResponse>

    suspend fun handleLogout(
        token: String,
        logoutRequestBody: LogoutRequestBody
    ): Response<LogoutResponse>

    suspend fun getBusinessLocation(token: String): Response<BusinessLocationResponse>

    suspend fun getCategory(token: String): Response<CategoryResponse>

    fun getAllProduct(name: String, categoryId: String): LiveData<PagingData<Data>>

    fun getAllDiscount(name: String): LiveData<PagingData<discount>>

    fun getAllCustomer(name: String): LiveData<PagingData<customer>>

    suspend fun getDetailCustomer(token: String, id: String): Response<CustomerDetailResponse>

    suspend fun getCustomerGroup(token: String): Response<CustomerGroupResponse>

    suspend fun postCustomer(
        token: String,
        customerRequestBody: CustomerRequestBody
    ): Response<AddCustomerResponse>

    fun getCartProduct(): LiveData<List<ProductTable>>
    suspend fun getCartProductById(id :String): ProductTable?

    suspend fun addToCart(productTable: ProductTable)
    suspend fun addListProductToCart(productTable: List<ProductTable>)

    suspend fun updateProduct(productTable: ProductTable?)

    suspend fun updateProductCart(listProductTable: List<ProductTable>?)

    suspend fun deleteProduct(productTable: ProductTable?)

    suspend fun deleteAllCart()

    fun getCustomerCart(): LiveData<CustomerTable?>

    suspend fun addCustomerCart(customerTable: CustomerTable)

    suspend fun postSell(
        token: String,
        transactionRequestBody: TransactionRequestBody
    ): Response<TransactionResponse>

    fun getHistorySell(): LiveData<PagingData<HistoryRow>>

    suspend fun getDetailHistory(token: String, id: String): Response<HistoryDetailResponse>

    suspend fun getCashAmount(token: String): Response<CashAmountResponse>

    suspend fun checkCashRegister(token: String): Response<CashRegisterCheckResponse>

    suspend fun insertDraft(draftTable: DraftTable)
    fun getDrafts(): LiveData<List<DraftTable>>
    suspend fun getDraftsById(id: Int): Response<DraftTable>

    suspend fun deleteDrafts()
}