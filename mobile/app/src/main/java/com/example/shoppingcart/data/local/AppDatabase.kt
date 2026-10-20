package com.example.shoppingcart.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shoppingcart.data.local.dao.CartDao
import com.example.shoppingcart.data.local.dao.ProductDao
import com.example.shoppingcart.data.local.entity.CartItemEntity
import com.example.shoppingcart.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, CartItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun cartDao(): CartDao

    companion object {
        const val NAME = "shopping_cart.db"
    }
}
