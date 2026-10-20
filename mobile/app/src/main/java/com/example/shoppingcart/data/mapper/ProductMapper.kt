package com.example.shoppingcart.data.mapper

import com.example.shoppingcart.data.local.entity.ProductEntity
import com.example.shoppingcart.domain.model.Money
import com.example.shoppingcart.domain.model.Product

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    description = description,
    price = Money(priceInCents),
    stock = stock
)
