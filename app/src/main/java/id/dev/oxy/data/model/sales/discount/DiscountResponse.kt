package id.dev.oxy.data.model.sales.discount


import com.google.gson.annotations.SerializedName

data class DiscountResponse(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("links")
    val links: Links,
    @SerializedName("meta")
    val meta: Meta
)