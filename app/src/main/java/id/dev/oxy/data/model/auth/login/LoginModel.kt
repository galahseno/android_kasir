package id.dev.oxy.data.model.auth.login

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @field:SerializedName("username")
    val username: String,
    @field:SerializedName("password")
    val password: String,
)
