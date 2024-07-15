package id.dev.oxy.data.model.customer.response


import com.google.gson.annotations.SerializedName
import id.dev.oxy.data.local.entity.CustomerTable

data class Data(
    @SerializedName("contact_id")
    val contactId: String,
    @SerializedName("contact_status")
    val contactStatus: String,
    @SerializedName("customer_group")
    val customerGroup: CustomerGroup?,
    @SerializedName("customer_group_id")
    val customerGroupId: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String
) {
    fun toCustomerTable(): CustomerTable {
        return CustomerTable(
            name = name,
            discountAmount = customerGroup?.amount
        )
    }
}