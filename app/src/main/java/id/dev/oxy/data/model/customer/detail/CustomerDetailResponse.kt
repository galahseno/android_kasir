package id.dev.oxy.data.model.customer.detail


import com.google.gson.annotations.SerializedName

data class CustomerDetailResponse(
    @SerializedName("data")
    val data: List<Data>
)