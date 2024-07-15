package id.dev.oxy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.dev.oxy.data.local.dao.CustomerDao
import id.dev.oxy.data.local.dao.DraftDao
import id.dev.oxy.data.local.dao.ProductDao
import id.dev.oxy.data.local.entity.CustomerTable
import id.dev.oxy.data.local.entity.DraftTable
import id.dev.oxy.data.local.entity.ProductTable
import id.dev.oxy.util.Converter

@Database(
    version = 1,
    entities = [ProductTable::class, CustomerTable::class, DraftTable::class],
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class OxyDb : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun draftDao(): DraftDao
}