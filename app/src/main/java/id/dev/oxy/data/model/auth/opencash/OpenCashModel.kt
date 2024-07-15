package id.dev.oxy.data.model.auth.opencash

import com.google.gson.annotations.SerializedName

data class OpenCashModel(
    @field:SerializedName("initial_amount")
    val initialAmount: Int,
    @field:SerializedName("location_id")
    val locationId: Int,
)