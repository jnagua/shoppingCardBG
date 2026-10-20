package com.example.shoppingcart.domain.usecase

import com.example.shoppingcart.domain.error.CartError
import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.model.Quantity
import com.example.shoppingcart.domain.repository.CartRepository
import com.example.shoppingcart.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Reemplaza la cantidad de un producto que ya está en el carrito.
 */
class UpdateCartItemQuantityUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: Long, quantity: Int): Cart {
        val validQuantity = Quantity(quantity)
        val product = productRepository.getProduct(productId)
            ?: throw CartError.ProductNotFound(productId)

        val updatedCart = cartRepository.getCart().updateQuantity(product, validQuantity)
        cartRepository.save(updatedCart)
        return updatedCart
    }
}
