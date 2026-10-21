package com.example.shoppingcart.testutil

import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.repository.CartRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Carrito en memoria para pruebas. Con pauseSaves() el guardado queda suspendido
 * hasta resumeSaves(), lo que permite comprobar el estado de operación pendiente.
 */
class FakeCartRepository(initialCart: Cart = Cart()) : CartRepository {

    private val cart = MutableStateFlow(initialCart)
    private var saveGate: CompletableDeferred<Unit>? = null

    val current: Cart get() = cart.value

    fun pauseSaves() {
        saveGate = CompletableDeferred()
    }

    fun resumeSaves() {
        saveGate?.complete(Unit)
        saveGate = null
    }

    override fun observeCart(): Flow<Cart> = cart

    override suspend fun getCart(): Cart = cart.value

    override suspend fun save(cart: Cart) {
        saveGate?.await()
        this.cart.value = cart
    }
}
