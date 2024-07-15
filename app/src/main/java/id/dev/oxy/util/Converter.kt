package id.dev.oxy.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.dev.oxy.data.local.entity.ProductTable

class Converter {

    @TypeConverter
    fun toListProductTable(value: String): List<ProductTable> {
        val type = object : TypeToken<List<ProductTable>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromListProductTable(list: List<ProductTable>): String = Gson().toJson(list)
}