package com.example.shoppingcart.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.shoppingcart.data.local.entity.CartItemEntity
import com.example.shoppingcart.data.local.entity.CartItemWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CartDao {

    @Transaction
    @Query("SELECT * FROM cart_items ORDER BY productId")
    abstract fun observeItems(): Flow<List<CartItemWithProduct>>

    @Transaction
    @Query("SELECT * FROM cart_items ORDER BY productId")
    abstract suspend fun getItems(): List<CartItemWithProduct>

    @Transaction
    open suspend fun replaceAll(items: List<CartItemEntity>) {
        clear()
        insertAll(items)
    }

    @Query("DELETE FROM cart_items")
    protected abstract suspend fun clear()

    @Insert
    protected abstract suspend fun insertAll(items: List<CartItemEntity>)
}
