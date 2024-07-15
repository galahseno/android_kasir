package id.dev.oxy.data.model.auth.login

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String
)