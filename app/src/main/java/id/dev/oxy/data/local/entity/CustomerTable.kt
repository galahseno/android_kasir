package id.dev.oxy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_table")
data class CustomerTable(
    @PrimaryKey
    @ColumnInfo(name = "customer_id")
    val customerId: Int = 1,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "discount_amount")
    val discountAmount: String? = null,
)
