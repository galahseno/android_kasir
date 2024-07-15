package id.dev.oxy.data.model.transaction


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("is_discount_member")
    val isDiscountMember: Int,
    @SerializedName("line_discount_amount")
    val lineDiscountAmount: String,
    @SerializedName("line_discount_member")
    val lineDiscountMember: String,
    @SerializedName("line_discount_type")
    val lineDiscountType: String,
    @SerializedName("line_total")
    val lineTotal: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("quantity")
    val quantity: String,
    @SerializedName("unit_price")
    val unitPrice: String,
    @SerializedName("unit_price_before_discount")
    val unitPriceBeforeDiscount: String,
    @SerializedName("variation_id")
    val variationId: String
)