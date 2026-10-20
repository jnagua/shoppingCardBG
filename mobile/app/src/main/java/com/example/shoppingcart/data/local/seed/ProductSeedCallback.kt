package com.example.shoppingcart.data.local.seed

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.shoppingcart.data.local.entity.ProductEntity
import org.json.JSONArray

/**
 * Carga el catálogo inicial desde assets/products.json la primera vez que se crea la base.
 */
class ProductSeedCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        readProducts().forEach { product ->
            db.insert(ProductEntity.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, product.toContentValues())
        }
    }

    private fun readProducts(): List<ProductEntity> {
        val json = context.assets.open(SEED_FILE).bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            ProductEntity(
                id = item.getLong("id"),
                name = item.getString("name"),
                description = item.getString("description"),
                priceInCents = item.getLong("priceInCents"),
                stock = item.getInt("stock")
            )
        }
    }

    private fun ProductEntity.toContentValues() = ContentValues().apply {
        put("id", id)
        put("name", name)
        put("description", description)
        put("priceInCents", priceInCents)
        put("stock", stock)
    }

    private companion object {
        const val SEED_FILE = "products.json"
    }
}
