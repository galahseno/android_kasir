package id.dev.oxy.data.model.customer.add

import com.google.gson.annotations.SerializedName

data class CustomerRequestBody(
    @SerializedName("address_line_1")
    val addressLine1: String,
    @SerializedName("address_line_2")
    val addressLine2: String,
    @SerializedName("customer_group_id")
    val customerGroupId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("middle_name")
    val middleName: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("prefix")
    val prefix: String
)