package id.dev.oxy.data.model.business_location


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("alternate_number")
    val alternateNumber: Any,
    @SerializedName("business_id")
    val businessId: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("custom_field1")
    val customField1: Any,
    @SerializedName("custom_field2")
    val customField2: Any,
    @SerializedName("custom_field3")
    val customField3: Any,
    @SerializedName("custom_field4")
    val customField4: Any,
    @SerializedName("default_payment_accounts")
    val defaultPaymentAccounts: String,
    @SerializedName("deleted_at")
    val deletedAt: Any,
    @SerializedName("email")
    val email: Any,
    @SerializedName("featured_products")
    val featuredProducts: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("invoice_layout_id")
    val invoiceLayoutId: String,
    @SerializedName("invoice_scheme_id")
    val invoiceSchemeId: String,
    @SerializedName("is_active")
    val isActive: String,
    @SerializedName("landmark")
    val landmark: String,
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("mobile")
    val mobile: Any,
    @SerializedName("name")
    val name: String,
    @SerializedName("print_receipt_on_invoice")
    val printReceiptOnInvoice: String,
    @SerializedName("printer_id")
    val printerId: Any,
    @SerializedName("receipt_printer_type")
    val receiptPrinterType: String,
    @SerializedName("sale_invoice_layout_id")
    val saleInvoiceLayoutId: String,
    @SerializedName("selling_price_group_id")
    val sellingPriceGroupId: Any,
    @SerializedName("state")
    val state: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("website")
    val website: Any,
    @SerializedName("zip_code")
    val zipCode: String
)