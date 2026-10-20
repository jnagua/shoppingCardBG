package com.example.shoppingcart.data.repository

import com.example.shoppingcart.data.SimulatedLatency
import com.example.shoppingcart.data.local.dao.CartDao
import com.example.shoppingcart.data.mapper.toDomain
import com.example.shoppingcart.data.mapper.toEntities
import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val simulatedLatency: SimulatedLatency
) : CartRepository {

    override fun observeCart(): Flow<Cart> =
        cartDao.observeItems()
            .onStart { simulatedLatency() }
            .map { it.toDomain() }

    override suspend fun getCart(): Cart = cartDao.getItems().toDomain()

    override suspend fun save(cart: Cart) {
        simulatedLatency()
        cartDao.replaceAll(cart.toEntities())
    }
}
