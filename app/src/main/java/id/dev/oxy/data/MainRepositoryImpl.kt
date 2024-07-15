package id.dev.oxy.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import id.dev.oxy.data.api.ApiService
import id.dev.oxy.data.local.DataStoreRepository
import id.dev.oxy.data.local.dao.CustomerDao
import id.dev.oxy.data.local.dao.DraftDao
import id.dev.oxy.data.local.dao.ProductDao
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
import id.dev.oxy.data.souce.CustomerPagingSource
import id.dev.oxy.data.souce.DiscountPagingSource
import id.dev.oxy.data.souce.HistoryPagingSource
import id.dev.oxy.data.souce.ProductPagingSource
import id.dev.oxy.util.Response
import id.dev.oxy.util.getErrorMessage
import javax.inject.Inject
import javax.inject.Singleton
import id.dev.oxy.data.model.customer.response.Data as customer
import id.dev.oxy.data.model.sales.discount.Data as discount

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreRepository: DataStoreRepository,
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val draftDao: DraftDao,
) : MainRepository {

    override suspend fun handleLogin(loginModel: LoginModel): Response<LoginResponse> {
        return try {
            Response.Success(apiService.loginAccount(loginModel))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun handleOpenCash(
        token: String,
        openCashModel: OpenCashModel
    ): Response<OpenCashResponse> {
        return try {
            Response.Success(apiService.openCashRegister(token, openCashModel))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun handleLogout(
        token: String,
        logoutRequestBody: LogoutRequestBody
    ): Response<LogoutResponse> {
        return try {
            Response.Success(apiService.logoutAccount(token, logoutRequestBody))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun getBusinessLocation(token: String): Response<BusinessLocationResponse> {
        return try {
            Response.Success(apiService.getBusinessLocation(token))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun getCategory(token: String): Response<CategoryResponse> {
        return try {
            Response.Success(apiService.getCategory(token))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override fun getAllProduct(name: String, categoryId: String): LiveData<PagingData<Data>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE),
            pagingSourceFactory = {
                ProductPagingSource(apiService, dataStoreRepository, name, categoryId)
            }
        ).liveData
    }

    override fun getAllDiscount(name: String): LiveData<PagingData<discount>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE),
            pagingSourceFactory = {
                DiscountPagingSource(apiService, dataStoreRepository, name)
            }
        ).liveData
    }

    override fun getAllCustomer(name: String): LiveData<PagingData<customer>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE),
            pagingSourceFactory = {
                CustomerPagingSource(apiService, dataStoreRepository, name)
            }
        ).liveData
    }

    override suspend fun getDetailCustomer(
        token: String,
        id: String
    ): Response<CustomerDetailResponse> {
        return try {
            Response.Success(apiService.getCustomerDetail(token, id))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun getCustomerGroup(token: String): Response<CustomerGroupResponse> {
        return try {
            Response.Success(apiService.getCustomerGroup(token))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun postCustomer(
        token: String,
        customerRequestBody: CustomerRequestBody
    ): Response<AddCustomerResponse> {
        return try {
            Response.Success(apiService.addCustomer(token, customerRequestBody))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override fun getCartProduct(): LiveData<List<ProductTable>> {
        return productDao.getAllProduct()
    }

    override suspend fun getCartProductById(id: String): ProductTable? {
       return productDao.getProductById(id)
    }

    override suspend fun addToCart(productTable: ProductTable) {
        productDao.insertProduct(productTable)
    }

    override suspend fun addListProductToCart(productTable: List<ProductTable>) {
        productDao.insertListProduct(productTable)
    }

    override suspend fun updateProduct(productTable: ProductTable?) {
        if (productTable != null) {
            productDao.updateProduct(productTable)
        }
    }

    override suspend fun updateProductCart(listProductTable: List<ProductTable>?) {
        if (listProductTable != null) {
            productDao.updateCartProduct(listProductTable)
        }
    }

    override suspend fun deleteProduct(productTable: ProductTable?) {
        if (productTable != null) {
            productDao.deleteProduct(productTable)
        }
    }

    override suspend fun deleteAllCart() {
        productDao.deleteAll()
        customerDao.deleteCustomer()
    }

    override fun getCustomerCart(): LiveData<CustomerTable?> {
        return customerDao.getCustomer()
    }

    override suspend fun addCustomerCart(customerTable: CustomerTable) {
        customerDao.insertCustomer(customerTable)
    }

    override suspend fun postSell(
        token: String,
        transactionRequestBody: TransactionRequestBody
    ): Response<TransactionResponse> {
        return try {
            Response.Success(apiService.postSell(token, transactionRequestBody))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override fun getHistorySell(): LiveData<PagingData<HistoryRow>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE),
            pagingSourceFactory = {
                HistoryPagingSource(apiService, dataStoreRepository)
            }
        ).liveData
    }

    override suspend fun getDetailHistory(
        token: String,
        id: String
    ): Response<HistoryDetailResponse> {
        return try {
            Response.Success(apiService.getHistoryDetail(token, id))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun getCashAmount(token: String): Response<CashAmountResponse> {
        return try {
            Response.Success(apiService.getCashAmount(token))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun checkCashRegister(token: String): Response<CashRegisterCheckResponse> {
        return try {
            Response.Success(apiService.checkCashRegister(token))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun insertDraft(draftTable: DraftTable) {
        draftDao.insertDraft(draftTable)
    }

    override fun getDrafts(): LiveData<List<DraftTable>> {
       return draftDao.getDrafts()
    }

    override suspend fun getDraftsById(id: Int): Response<DraftTable> {
        return try {
            Response.Success(draftDao.getDraftsById(id))
        } catch (e: Exception) {
            Response.Error(e.getErrorMessage())
        }
    }

    override suspend fun deleteDrafts() {
        draftDao.deleteDrafts()
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}