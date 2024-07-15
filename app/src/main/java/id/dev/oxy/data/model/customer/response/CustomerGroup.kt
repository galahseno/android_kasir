package id.dev.oxy.data.model.customer.response


import com.google.gson.annotations.SerializedName

data class CustomerGroup(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price_calculation_type")
    val priceCalculationType: String
)