package id.dev.oxy.data.model.customer.detail


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address_line_1")
    val addressLine1: String?,
    @SerializedName("address_line_2")
    val addressLine2: String?,
    @SerializedName("alternate_number")
    val alternateNumber: Any,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("business_id")
    val businessId: String,
    @SerializedName("city")
    val city: Any,
    @SerializedName("contact_id")
    val contactId: String,
    @SerializedName("contact_status")
    val contactStatus: String,
    @SerializedName("country")
    val country: Any,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("credit_limit")
    val creditLimit: String,
    @SerializedName("custom_field1")
    val customField1: Any,
    @SerializedName("custom_field10")
    val customField10: Any,
    @SerializedName("custom_field2")
    val customField2: Any,
    @SerializedName("custom_field3")
    val customField3: Any,
    @SerializedName("custom_field4")
    val customField4: Any,
    @SerializedName("custom_field5")
    val customField5: Any,
    @SerializedName("custom_field6")
    val customField6: Any,
    @SerializedName("custom_field7")
    val customField7: Any,
    @SerializedName("custom_field8")
    val customField8: Any,
    @SerializedName("custom_field9")
    val customField9: Any,
    @SerializedName("customer_group")
    val customerGroup: String?,
    @SerializedName("customer_group_id")
    val customerGroupId: Any,
    @SerializedName("deleted_at")
    val deletedAt: Any,
    @SerializedName("dob")
    val dob: Any,
    @SerializedName("email")
    val email: String?,
    @SerializedName("export_custom_field_1")
    val exportCustomField1: Any,
    @SerializedName("export_custom_field_2")
    val exportCustomField2: Any,
    @SerializedName("export_custom_field_3")
    val exportCustomField3: Any,
    @SerializedName("export_custom_field_4")
    val exportCustomField4: Any,
    @SerializedName("export_custom_field_5")
    val exportCustomField5: Any,
    @SerializedName("export_custom_field_6")
    val exportCustomField6: Any,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("invoice_received")
    val invoiceReceived: String,
    @SerializedName("is_default")
    val isDefault: String,
    @SerializedName("is_export")
    val isExport: String,
    @SerializedName("landline")
    val landline: Any,
    @SerializedName("last_name")
    val lastName: Any,
    @SerializedName("max_transaction_date")
    val maxTransactionDate: Any,
    @SerializedName("middle_name")
    val middleName: Any,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("opening_balance")
    val openingBalance: String,
    @SerializedName("opening_balance_paid")
    val openingBalancePaid: String,
    @SerializedName("pay_term_number")
    val payTermNumber: Any,
    @SerializedName("pay_term_type")
    val payTermType: String,
    @SerializedName("position")
    val position: Any,
    @SerializedName("prefix")
    val prefix: String,
    @SerializedName("purchase_due")
    val purchaseDue: Int,
    @SerializedName("purchase_return_due")
    val purchaseReturnDue: Int,
    @SerializedName("sell_due")
    val sellDue: Int,
    @SerializedName("sell_return_due")
    val sellReturnDue: Int,
    @SerializedName("sell_return_paid")
    val sellReturnPaid: String,
    @SerializedName("shipping_address")
    val shippingAddress: Any,
    @SerializedName("shipping_custom_field_details")
    val shippingCustomFieldDetails: Any,
    @SerializedName("state")
    val state: Any,
    @SerializedName("supplier_business_name")
    val supplierBusinessName: Any,
    @SerializedName("tax_number")
    val taxNumber: Any,
    @SerializedName("total_invoice")
    val totalInvoice: String,
    @SerializedName("total_ledger_discount")
    val totalLedgerDiscount: String,
    @SerializedName("total_rp")
    val totalRp: String,
    @SerializedName("total_rp_expired")
    val totalRpExpired: String,
    @SerializedName("total_rp_used")
    val totalRpUsed: String,
    @SerializedName("total_sell_return")
    val totalSellReturn: String,
    @SerializedName("transaction_date")
    val transactionDate: Any,
    @SerializedName("type")
    val type: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("zip_code")
    val zipCode: Any
)