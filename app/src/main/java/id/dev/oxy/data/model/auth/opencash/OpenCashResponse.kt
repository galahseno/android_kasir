package id.dev.oxy.data.model.auth.opencash


import com.google.gson.annotations.SerializedName

data class OpenCashResponse(
    @SerializedName("data")
    val data: Data
)