package com.example.shoppingcart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ProductEntity.TABLE_NAME)
data class ProductEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String,
    val priceInCents: Long,
    val stock: Int
) {
    companion object {
        const val TABLE_NAME = "products"
    }
}
