package id.dev.oxy.data.model.sales.product


import com.google.gson.annotations.SerializedName

data class Unit(
    @SerializedName("actual_name")
    val actualName: String,
    @SerializedName("allow_decimal")
    val allowDecimal: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("short_name")
    val shortName: String
)