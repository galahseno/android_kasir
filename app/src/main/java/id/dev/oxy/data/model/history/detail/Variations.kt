package id.dev.oxy.data.model.history.detail


import com.google.gson.annotations.SerializedName

data class Variations(
    @SerializedName("id")
    val id: Int,
    @SerializedName("sell_price_inc_tax")
    val sellPriceIncTax: String
)