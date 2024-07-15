package id.dev.oxy.data.model.auth.logout


import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("message")
    val message: String
)