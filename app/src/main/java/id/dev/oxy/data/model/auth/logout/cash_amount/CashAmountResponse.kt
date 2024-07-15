package id.dev.oxy.data.model.auth.logout.cash_amount


import com.google.gson.annotations.SerializedName

data class CashAmountResponse(
    @SerializedName("success")
    val success: Int,
    @SerializedName("total_cash")
    val totalCash: Int
)