package id.dev.oxy.data.model.history.detail


import com.google.gson.annotations.SerializedName

data class PaymentLine(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("card_number")
    val cardNumber: String,
    @SerializedName("card_type")
    val cardType: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("method")
    val method: String,
    @SerializedName("transaction_id")
    val transactionId: String
)