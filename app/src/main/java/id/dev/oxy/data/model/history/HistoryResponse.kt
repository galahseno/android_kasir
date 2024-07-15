package id.dev.oxy.data.model.history


import com.google.gson.annotations.SerializedName

data class HistoryResponse(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("links")
    val links: Links,
    @SerializedName("meta")
    val meta: Meta
)