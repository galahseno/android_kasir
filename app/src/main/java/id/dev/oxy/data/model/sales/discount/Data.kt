package id.dev.oxy.data.model.sales.discount


import com.google.gson.annotations.SerializedName
import id.dev.oxy.data.local.entity.ProductTable
import kotlin.math.absoluteValue

data class Data(
    @SerializedName("brand")
    val brand: Any?,
    @SerializedName("category")
    val category: Any?,
    @SerializedName("discount_amount")
    val discountAmount: String,
    @SerializedName("discount_type")
    val discountType: String,
    @SerializedName("ends_at")
    val endsAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active")
    val isActive: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("priority")
    val priority: String,
    @SerializedName("starts_at")
    val startsAt: String
) {
    fun getTotalPriceAfterDiscount(product: ProductTable) : Double {
        return when (discountType) {
            DiscountType.percentage.name -> {
                val percentProduct =
                    discountAmount.toDouble().absoluteValue.div(100)
                val totalProductPrice =
                    product.unitPriceBeforeDiscount.times(product.quantity)
                val discountPercentProduct =
                    totalProductPrice.times(percentProduct)

                val totalPrice =
                    totalProductPrice.minus(discountPercentProduct)
                totalPrice
            }
            DiscountType.fixed.name -> {
                val fixedProduct =
                    discountAmount.toDouble().absoluteValue
                val totalProductPrice =
                    product.unitPriceBeforeDiscount.minus(fixedProduct)

                val totalPrice =
                    totalProductPrice.times(product.quantity)
                if (totalPrice > 0.0) totalPrice else 0.0
            }
            else -> product.unitPriceBeforeDiscount.times(product.quantity)
        }
    }
}