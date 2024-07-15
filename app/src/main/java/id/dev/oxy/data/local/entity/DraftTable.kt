package id.dev.oxy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "draft_table")
data class DraftTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "customer_id")
    val customerId: Int = 1,

    @ColumnInfo(name = "customer_name")
    val customerName: String,

    @ColumnInfo(name = "customer_discount_amount")
    val customerDiscountAmount: String? = null,

    @ColumnInfo(name = "list_product")
    val products: List<ProductTable>,
)