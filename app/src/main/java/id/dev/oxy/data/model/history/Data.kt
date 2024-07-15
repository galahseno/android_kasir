package id.dev.oxy.data.model.history


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("business_id")
    val businessId: String,
    @SerializedName("contact_id")
    val contactId: String,
    @SerializedName("final_total")
    val finalTotal: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("invoice_no")
    val invoiceNo: String,
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("transaction_date")
    val transactionDate: String,
    @SerializedName("type")
    val type: String
)