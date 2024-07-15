package id.dev.oxy.data.model.transaction


import com.google.gson.annotations.SerializedName

data class TransactionRequestBody(
    @SerializedName("sells")
    val sells: List<Sell>
)