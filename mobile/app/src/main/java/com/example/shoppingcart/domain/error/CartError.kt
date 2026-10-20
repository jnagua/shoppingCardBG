package com.example.shoppingcart.domain.error

/**
 * Errores de negocio del carrito. La capa de UI los traduce a mensajes legibles.
 */
sealed class CartError(message: String) : Exception(message) {

    class InvalidQuantity(val quantity: Int) :
        CartError("La cantidad debe ser mayor que cero: $quantity")

    class ProductNotFound(val productId: Long) :
        CartError("Producto no encontrado: $productId")

    class ItemNotInCart(val productId: Long) :
        CartError("El producto $productId no está en el carrito")

    class StockExceeded(val productId: Long, val stock: Int, val requested: Int) :
        CartError("Se solicitaron $requested unidades del producto $productId, pero solo hay $stock disponibles")
}
