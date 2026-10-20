package com.example.shoppingcart.domain.model

import com.example.shoppingcart.domain.error.CartError

/**
 * Clase carrito es inmutable
 * Garantiza una sola línea por producto y que ninguna supere el stock.
 */
class Cart(items: List<CartItem> = emptyList()) {

    val items: List<CartItem> = items.toList()

    init {
        require(this.items.distinctBy { it.productId }.size == this.items.size) {
            "no se puede tener duplicados"
        }
    }

    val totalUnits: Int get() = items.sumOf { it.quantity.value }

    val total: Money get() = items.fold(Money.ZERO) { acc, item -> acc + item.lineTotal }

    fun isEmpty(): Boolean = items.isEmpty()

    private fun findItem(productId: Long): CartItem? = items.find { it.productId == productId }

    private fun replaceItem(item: CartItem): Cart =
        Cart(items.map { if (it.productId == item.productId) item else it })

    private fun ensureWithinStock(product: Product, quantity: Quantity) {
        if (quantity.value > product.stock) {
            throw CartError.StockExceeded(product.id, product.stock, quantity.value)
        }
    }

    fun add(product: Product, quantity: Quantity): Cart {
        val current = findItem(product.id)
        val newQuantity = current?.let { it.quantity + quantity } ?: quantity
        ensureWithinStock(product, newQuantity)

        val item = CartItem(product.id, product.name, product.price, newQuantity)
        return if (current == null) {
            Cart(items + item)
        } else {
            replaceItem(item)
        }
    }

    fun updateQuantity(product: Product, quantity: Quantity): Cart {
        if (findItem(product.id) == null) throw CartError.ItemNotInCart(product.id)
        ensureWithinStock(product, quantity)
        return replaceItem(CartItem(product.id, product.name, product.price, quantity))
    }

    fun remove(productId: Long): Cart {
        if (findItem(productId) == null) throw CartError.ItemNotInCart(productId)
        return Cart(items.filterNot { it.productId == productId })
    }
}
