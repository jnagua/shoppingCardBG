package com.example.shoppingcart.ui.cart

/**
 * Datos ya preparados para pintar una línea del carrito.
 */
data class CartItemUi(
    val productId: Long,
    val name: String,
    val unitPrice: String,
    val quantity: Int,
    val lineTotal: String,
    val canIncrease: Boolean,
    val canDecrease: Boolean,
    val canRemove: Boolean,
    val isPending: Boolean
)
