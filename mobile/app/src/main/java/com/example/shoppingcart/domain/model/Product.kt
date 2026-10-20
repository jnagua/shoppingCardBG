package com.example.shoppingcart.domain.model

/**
 * Producto del catálogo. Su precio y stock son la fuente confiable para el carrito.
 */
data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val price: Money,
    val stock: Int
) {
    init {
        require(stock >= 0) { "Stock no puede ser negativo: $stock" }
    }
}
