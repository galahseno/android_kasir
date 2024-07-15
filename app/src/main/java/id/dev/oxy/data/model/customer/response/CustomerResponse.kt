package id.dev.oxy.data.model.customer.response


import com.google.gson.annotations.SerializedName

data class CustomerResponse(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("links")
    val links: Links,
    @SerializedName("meta")
    val meta: Meta
)