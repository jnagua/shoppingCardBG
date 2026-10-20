package com.example.shoppingcart.domain.model

/**
 * Línea del carrito: un producto con su precio unitario y la cantidad agregada.
 */
data class CartItem(
    val productId: Long,
    val name: String,
    val unitPrice: Money,
    val quantity: Quantity
) {
    val lineTotal: Money get() = unitPrice * quantity
}
