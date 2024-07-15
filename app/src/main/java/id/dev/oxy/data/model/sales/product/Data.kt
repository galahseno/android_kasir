package id.dev.oxy.data.model.sales.product


import com.google.gson.annotations.SerializedName
import id.dev.oxy.data.local.entity.ProductTable

data class Data(
    @SerializedName("alert_quantity")
    val alertQuantity: String,
    @SerializedName("barcode_type")
    val barcodeType: String,
    @SerializedName("category")
    val category: Category,
    @SerializedName("enable_stock")
    val enableStock: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("is_inactive")
    val isInactive: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("sku")
    val sku: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("unit")
    val unit: Unit,
    @SerializedName("variations")
    val variations: List<Variation>,
    @SerializedName("weight")
    val weight: String?
) {
    fun toProductTable(): ProductTable {
        return ProductTable(
            productId = id.toString(),
            productName = name,
            variationId = variations[0].id.toString(),
            quantity = 1,
            unitPriceBeforeDiscount = variations[0].defaultSellPrice.toDouble(),
            unitPrice = variations[0].defaultSellPrice.toDouble(),
            lineDiscountType = "",
            lineDiscountAmount = 0.0,
            lineDiscountMember = 0.0,
            isDiscountMember = false,
            lineTotal = variations[0].defaultSellPrice.toDouble()
        )
    }

    fun toProductTableWithDiscountMember(lineDiscountMember: Double, lineTotal: Double): ProductTable {
        return ProductTable(
            productId = id.toString(),
            productName = name,
            variationId = variations[0].id.toString(),
            quantity = 1,
            unitPriceBeforeDiscount = variations[0].defaultSellPrice.toDouble(),
            unitPrice = lineTotal,
            lineDiscountType = "",
            lineDiscountAmount = 0.0,
            lineDiscountMember = lineDiscountMember,
            isDiscountMember = true,
            lineTotal = lineTotal
        )
    }
}