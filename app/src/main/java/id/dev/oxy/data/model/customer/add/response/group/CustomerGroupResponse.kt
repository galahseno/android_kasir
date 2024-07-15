package id.dev.oxy.data.model.customer.add.response.group


import com.google.gson.annotations.SerializedName

data class CustomerGroupResponse(
    @SerializedName("data")
    val data: List<Data>
)