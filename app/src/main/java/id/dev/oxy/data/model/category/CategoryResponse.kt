package id.dev.oxy.data.model.category


import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("data")
    val data: List<Data>
)