package id.dev.oxy.data.model.transaction.response


import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("sell")
    val sell: List<Sell>,
    @SerializedName("success")
    val success: Int
)