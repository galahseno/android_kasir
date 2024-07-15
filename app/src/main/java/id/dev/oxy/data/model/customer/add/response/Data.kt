package id.dev.oxy.data.model.customer.add.response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address_line_1")
    val addressLine1: String,
    @SerializedName("address_line_2")
    val addressLine2: String,
    @SerializedName("business_id")
    val businessId: String,
    @SerializedName("contact_id")
    val contactId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_by")
    val createdBy: Int,
    @SerializedName("credit_limit")
    val creditLimit: Any?,
    @SerializedName("customer_group_id")
    val customerGroupId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("middle_name")
    val middleName: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("prefix")
    val prefix: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("updated_at")
    val updatedAt: String
)