package id.dev.oxy.data.model.transaction.response


import com.google.gson.annotations.SerializedName
data class Sell(
    @SerializedName("contact")
    val contact: Contact,
    @SerializedName("contact_id")
    val contactId: String,
    @SerializedName("discount_amount")
    val discountAmount: String,
    @SerializedName("final_total")
    val finalTotal: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("invoice_no")
    val invoiceNo: String,
    @SerializedName("location")
    val location: Location,
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("payment_lines")
    val paymentLines: List<PaymentLine>,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("sell_lines")
    val sellLines: List<SellLine>,
    @SerializedName("status")
    val status: String,
    @SerializedName("transaction_date")
    val transactionDate: String,
    @SerializedName("type")
    val type: String
)