package com.example.shoppingcart.domain.repository

import com.example.shoppingcart.domain.model.Cart
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun observeCart(): Flow<Cart>

    suspend fun getCart(): Cart

    suspend fun save(cart: Cart)
}
