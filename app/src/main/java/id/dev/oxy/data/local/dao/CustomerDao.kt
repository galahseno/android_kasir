package id.dev.oxy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import id.dev.oxy.data.local.entity.CustomerTable

@Dao
interface CustomerDao {
    @Query("SELECT * from customer_table limit 1")
    fun getCustomer(): LiveData<CustomerTable?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customerTable: CustomerTable)

    @Query("DELETE FROM customer_table")
    suspend fun deleteCustomer()
}