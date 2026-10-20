package com.example.shoppingcart.domain.usecase

import com.example.shoppingcart.domain.error.CartError
import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.model.Quantity
import com.example.shoppingcart.domain.repository.CartRepository
import com.example.shoppingcart.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Agrega un producto del catálogo al carrito, o incrementa su cantidad si ya está.
 */
class AddToCartUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: Long, quantity: Int): Cart {
        val validQuantity = Quantity(quantity)
        val product = productRepository.getProduct(productId)
            ?: throw CartError.ProductNotFound(productId)

        val updatedCart = cartRepository.getCart().add(product, validQuantity)
        cartRepository.save(updatedCart)
        return updatedCart
    }
}
