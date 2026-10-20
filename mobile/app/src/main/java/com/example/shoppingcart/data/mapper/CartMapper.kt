package com.example.shoppingcart.data.mapper

import com.example.shoppingcart.data.local.entity.CartItemEntity
import com.example.shoppingcart.data.local.entity.CartItemWithProduct
import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.model.CartItem
import com.example.shoppingcart.domain.model.Money
import com.example.shoppingcart.domain.model.Quantity

fun List<CartItemWithProduct>.toDomain(): Cart = Cart(
    map { row ->
        CartItem(
            productId = row.product.id,
            name = row.product.name,
            unitPrice = Money(row.product.priceInCents),
            quantity = Quantity(row.item.quantity)
        )
    }
)

fun Cart.toEntities(): List<CartItemEntity> =
    items.map { CartItemEntity(productId = it.productId, quantity = it.quantity.value) }
