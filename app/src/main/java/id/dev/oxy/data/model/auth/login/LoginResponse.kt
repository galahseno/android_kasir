package id.dev.oxy.data.model.auth.login


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("token")
    val token: String,
    @SerializedName("cash_register")
    val cashRegister: String
)