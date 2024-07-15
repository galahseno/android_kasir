package id.dev.oxy.data.model.sales.cash_register


import com.google.gson.annotations.SerializedName

data class CashRegisterCheckResponse(
    @SerializedName("card")
    val card: String,
    @SerializedName("cash")
    val cash: String,
    @SerializedName("cash_register")
    val cashRegister: String,
    @SerializedName("e_wallet")
    val eWallet: String,
    @SerializedName("success")
    val success: Int,
    @SerializedName("total_cash")
    val totalCash: Int,
    @SerializedName("total_payment")
    val totalPayment: Int
)