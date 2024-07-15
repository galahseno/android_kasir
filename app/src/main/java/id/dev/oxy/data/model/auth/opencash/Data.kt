package id.dev.oxy.data.model.auth.opencash


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("business_id")
    val businessId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int
)