package id.dev.oxy.data.model.transaction.response


import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("city")
    val city: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("landmark")
    val landmark: Any?,
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("name")
    val name: String
)