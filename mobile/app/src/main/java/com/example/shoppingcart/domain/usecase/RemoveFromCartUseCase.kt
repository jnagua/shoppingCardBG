package com.example.shoppingcart.domain.usecase

import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: Long): Cart {
        val updatedCart = cartRepository.getCart().remove(productId)
        cartRepository.save(updatedCart)
        return updatedCart
    }
}
