package id.dev.oxy.data.model.exception


import com.google.gson.annotations.SerializedName

data class ErrorMessage(
    @SerializedName("message")
    val message: String
)