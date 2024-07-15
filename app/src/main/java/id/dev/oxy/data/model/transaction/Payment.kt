package id.dev.oxy.data.model.transaction


import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("card_number")
    val cardNumber: String,
    @SerializedName("card_type")
    val cardType: String = "",
    @SerializedName("method")
    val method: String
)