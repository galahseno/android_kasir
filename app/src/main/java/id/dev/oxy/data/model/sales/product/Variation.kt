package id.dev.oxy.data.model.sales.product


import com.google.gson.annotations.SerializedName

data class Variation(
    @SerializedName("default_sell_price")
    val defaultSellPrice: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("sell_price_inc_tax")
    val sellPriceIncTax: String
)