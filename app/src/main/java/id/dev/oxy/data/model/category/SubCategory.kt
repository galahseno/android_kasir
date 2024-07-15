package id.dev.oxy.data.model.category


import com.google.gson.annotations.SerializedName

data class SubCategory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("parent_id")
    val parentId: String
)