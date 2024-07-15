package id.dev.oxy.data.model.customer.add.response


import com.google.gson.annotations.SerializedName

data class AddCustomerResponse(
    @SerializedName("data")
    val data: Data
)