package com.example.shoppingcart.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CartItemWithProduct(
    @Embedded val item: CartItemEntity,
    @Relation(parentColumn = "productId", entityColumn = "id")
    val product: ProductEntity
)
