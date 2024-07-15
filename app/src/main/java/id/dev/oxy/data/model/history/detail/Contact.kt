package id.dev.oxy.data.model.history.detail


import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("contact_id")
    val contactId: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String
)