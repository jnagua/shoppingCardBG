package com.example.shoppingcart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Línea persistida del carrito. Solo guarda la cantidad: nombre y precio
 * se leen siempre del catálogo.
 */
@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItemEntity(
    @PrimaryKey val productId: Long,
    val quantity: Int
)
