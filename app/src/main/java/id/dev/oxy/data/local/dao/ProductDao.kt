package id.dev.oxy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import id.dev.oxy.data.local.entity.ProductTable

@Dao
interface ProductDao {
    @Query("SELECT * from product_table")
    fun getAllProduct(): LiveData<List<ProductTable>>

    @Query("SELECT * from product_table WHERE product_id == :id")
    suspend fun getProductById(id: String): ProductTable?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(productTable: ProductTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListProduct(productTable: List<ProductTable>)

    @Update
    suspend fun updateProduct(productTable: ProductTable)

    @Update
    suspend fun updateCartProduct(listProductTable: List<ProductTable>)

    @Delete
    suspend fun deleteProduct(productTable: ProductTable)

    @Query("DELETE FROM product_table")
    suspend fun deleteAll()
}