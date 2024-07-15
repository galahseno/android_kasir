package id.dev.oxy.data.model.customer.add.response.group


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price_calculation_type")
    val priceCalculationType: String
)