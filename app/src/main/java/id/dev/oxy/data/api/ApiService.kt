package id.dev.oxy.data.api

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
import id.dev.oxy.data.model.customer.response.CustomerResponse
import id.dev.oxy.data.model.history.HistoryResponse
import id.dev.oxy.data.model.history.detail.HistoryDetailResponse
import id.dev.oxy.data.model.sales.cash_register.CashRegisterCheckResponse
import id.dev.oxy.data.model.sales.discount.DiscountResponse
import id.dev.oxy.data.model.sales.product.ProductResponse
import id.dev.oxy.data.model.transaction.TransactionRequestBody
import id.dev.oxy.data.model.transaction.response.TransactionResponse
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    @Headers("Accept: application/json")
    suspend fun loginAccount(@Body requestBody: LoginModel): LoginResponse

    @POST("cash-register")
    @Headers("Accept: application/json")
    suspend fun openCashRegister(
        @Header("Authorization") token: String,
        @Body requestBody: OpenCashModel
    ): OpenCashResponse

    @POST("auth/logout")
    @Headers("Accept: application/json")
    suspend fun logoutAccount(
        @Header("Authorization") token: String,
        @Body requestBody: LogoutRequestBody
    ): LogoutResponse

    @GET("user/business-location")
    @Headers("Accept: application/json")
    suspend fun getBusinessLocation(@Header("Authorization") token: String): BusinessLocationResponse

    @GET("category")
    @Headers("Accept: application/json")
    suspend fun getCategory(@Header("Authorization") token: String): CategoryResponse

    @GET("product")
    @Headers("Accept: application/json")
    suspend fun getProduct(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("per_page") size: String,
        @Query("name") name: String = "",
        @Query("sku") sku: String = "",
        @Query("category_id") categoryId: String = "",
        @Query("order_by") orderBy: String = "product_name",
        @Query("order_direction") orderDirection: String = "asc",
    ): ProductResponse

    @GET("discount")
    @Headers("Accept: application/json")
    suspend fun getDiscount(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("per_page") size: String,
        @Query("name") name: String = "",
    ): DiscountResponse

    @GET("customer")
    @Headers("Accept: application/json")
    suspend fun getCustomer(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("per_page") size: String,
        @Query("name") name: String = "",
    ): CustomerResponse

    @POST("customer")
    @Headers("Accept: application/json")
    suspend fun addCustomer(
        @Header("Authorization") token: String,
        @Body requestBody: CustomerRequestBody
    ): AddCustomerResponse

    @GET("customer/{id}")
    @Headers("Accept: application/json")
    suspend fun getCustomerDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): CustomerDetailResponse

    @GET("customer-group")
    @Headers("Accept: application/json")
    suspend fun getCustomerGroup(@Header("Authorization") token: String): CustomerGroupResponse

    @POST("sell")
    @Headers("Accept: application/json")
    suspend fun postSell(
        @Header("Authorization") token: String,
        @Body requestBody: TransactionRequestBody
    ): TransactionResponse

    @GET("sell")
    @Headers("Accept: application/json")
    suspend fun getHistorySell(
        @Header("Authorization") token: String,
        @Query("page") page: String,
        @Query("per_page") size: String,
        @Query("order_by_date") date: String = "desc",
        @Query("start_date") startDate: String = "2023-01-01",
    ): HistoryResponse

    @GET("sell/{id}")
    @Headers("Accept: application/json")
    suspend fun getHistoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): HistoryDetailResponse

    @GET("cash-register-amount")
    @Headers("Accept: application/json")
    suspend fun getCashAmount(@Header("Authorization") token: String): CashAmountResponse

    @GET("cash-register-check")
    @Headers("Accept: application/json")
    suspend fun checkCashRegister(@Header("Authorization") token: String): CashRegisterCheckResponse
}