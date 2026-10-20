package com.example.shoppingcart.domain.usecase

import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<Cart> = cartRepository.observeCart()
}
