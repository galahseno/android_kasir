package id.dev.oxy.data.model.business_location


import com.google.gson.annotations.SerializedName

data class BusinessLocationResponse(
    @SerializedName("data")
    val data: List<Data>
)