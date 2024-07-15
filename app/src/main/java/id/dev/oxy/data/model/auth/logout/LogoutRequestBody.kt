package id.dev.oxy.data.model.auth.logout


import com.google.gson.annotations.SerializedName

data class LogoutRequestBody(
    @SerializedName("closing_amount")
    val closingAmount: String,
    @SerializedName("closing_note")
    val closingNote: String
)