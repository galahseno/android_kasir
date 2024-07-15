package id.dev.oxy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import id.dev.oxy.data.model.sales.discount.DiscountType
import id.dev.oxy.data.model.transaction.Product
import id.dev.oxy.util.int
import kotlin.math.absoluteValue

@Entity(tableName = "product_table")
data class ProductTable(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: String,

    @ColumnInfo(name = "product_name")
    val productName: String,

    @ColumnInfo(name = "variation_id")
    val variationId: String,

    @ColumnInfo(name = "quantity")
    var quantity: Int,

    @ColumnInfo(name = "unit_price_before_discount")
    val unitPriceBeforeDiscount: Double,

    @ColumnInfo(name = "unit_price")
    var unitPrice: Double,

    @ColumnInfo(name = "line_discount_type")
    val lineDiscountType: String,

    @ColumnInfo(name = "line_discount_amount")
    val lineDiscountAmount: Double,

    @ColumnInfo(name = "line_discount_member")
    var lineDiscountMember: Double,

    @ColumnInfo(name = "is_discount_member")
    var isDiscountMember: Boolean,

    @ColumnInfo(name = "line_total")
    var lineTotal: Double = (quantity * unitPrice),

    @ColumnInfo(name = "discount_product")
    var discountProduct: String? = null,
) {
    fun toProduct(): Product {
        return Product(
            productId = productId,
            variationId = variationId,
            quantity = quantity.toString(),
            unitPriceBeforeDiscount = unitPriceBeforeDiscount.toString(),
            unitPrice = unitPrice.toString(),
            lineDiscountType = lineDiscountType,
            lineDiscountAmount = lineDiscountAmount.toString(),
            lineDiscountMember = lineDiscountMember.toString(),
            isDiscountMember = isDiscountMember.int,
            lineTotal = lineTotal.toString()
        )
    }

    fun getTotalPriceAfterDiscount() : Double {
        return when (lineDiscountType) {
            DiscountType.percentage.name -> {
                val percentProduct =
                    lineDiscountAmount.absoluteValue.div(100)
                val totalProductPrice =
                    unitPriceBeforeDiscount.times(quantity)
                val discountPercentProduct =
                    totalProductPrice.times(percentProduct)

                val totalPrice =
                    totalProductPrice.minus(discountPercentProduct)
                totalPrice
            }
            DiscountType.fixed.name -> {
                val fixedProduct =
                    lineDiscountAmount.absoluteValue
                val totalProductPrice =
                    unitPriceBeforeDiscount.minus(fixedProduct)

                val totalPrice =
                    totalProductPrice.times(quantity)
                if (totalPrice > 0.0) totalPrice else 0.0
            }
            else -> unitPriceBeforeDiscount.times(quantity)
        }
    }

    fun getTotalPriceAfterDiscountWithQuantity(newQuantity: Int) : Double {
        return when (lineDiscountType) {
            DiscountType.percentage.name -> {
                val percentProduct =
                    lineDiscountAmount.absoluteValue.div(100)
                val totalProductPrice =
                    unitPriceBeforeDiscount.times(newQuantity)
                val discountPercentProduct =
                    totalProductPrice.times(percentProduct)

                val totalPrice =
                    totalProductPrice.minus(discountPercentProduct)
                totalPrice
            }
            DiscountType.fixed.name -> {
                val fixedProduct =
                    lineDiscountAmount.absoluteValue
                val totalProductPrice =
                    unitPriceBeforeDiscount.minus(fixedProduct)

                val totalPrice =
                    totalProductPrice.times(newQuantity)
                if (totalPrice > 0.0) totalPrice else 0.0
            }
            else -> unitPriceBeforeDiscount.times(newQuantity)
        }
    }
}
