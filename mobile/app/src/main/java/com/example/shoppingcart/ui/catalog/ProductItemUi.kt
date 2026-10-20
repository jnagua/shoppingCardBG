package com.example.shoppingcart.ui.catalog

/**
 * Datos ya preparados para pintar una tarjeta del catálogo.
 */
data class ProductItemUi(
    val id: Long,
    val name: String,
    val description: String,
    val price: String,
    val stock: Int,
    val quantityInCart: Int,
    val isAdding: Boolean
) {
    val canAdd: Boolean get() = !isAdding && quantityInCart < stock
}
