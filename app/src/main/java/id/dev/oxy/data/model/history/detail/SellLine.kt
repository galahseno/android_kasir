package id.dev.oxy.data.model.history.detail


import com.google.gson.annotations.SerializedName

data class SellLine(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_discount_member")
    val isDiscountMember: String,
    @SerializedName("line_discount_amount")
    val lineDiscountAmount: String,
    @SerializedName("line_discount_member")
    val lineDiscountMember: String,
    @SerializedName("line_discount_type")
    val lineDiscountType: String,
    @SerializedName("product")
    val product: Product,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("quantity")
    val quantity: String,
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("unit_price")
    val unitPrice: String,
    @SerializedName("unit_price_before_discount")
    val unitPriceBeforeDiscount: String,
    @SerializedName("variation_id")
    val variationId: String,
    @SerializedName("variations")
    val variations: Variations
)