package id.dev.oxy.data.model.history.detail


import com.google.gson.annotations.SerializedName

data class HistoryDetailResponse(
    @SerializedName("data")
    val data: List<Data>
)