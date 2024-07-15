package id.dev.oxy.data.model.transaction


import com.google.gson.annotations.SerializedName

data class Sell(
    @SerializedName("contact_id")
    val contactId: String,
    @SerializedName("discount_amount")
    val discountAmount: String,
    @SerializedName("discount_type")
    val discountType: String,
    @SerializedName("final_total")
    val finalTotal: String,
    @SerializedName("is_created_from_api")
    val isCreatedFromApi: Int,
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("payments")
    val payments: List<Payment>,
    @SerializedName("products")
    val products: List<Product>,
    @SerializedName("status")
    val status: String
)