package com.example.shoppingcart.testutil

import com.example.shoppingcart.domain.model.Product
import com.example.shoppingcart.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import java.io.IOException

/**
 * Catálogo en memoria para pruebas. Con failOnObserve simula un error de carga.
 */
class FakeProductRepository(products: List<Product> = emptyList()) : ProductRepository {

    private val products = MutableStateFlow(products)
    var failOnObserve = false

    override fun observeProducts(): Flow<List<Product>> = flow {
        if (failOnObserve) throw IOException("Error simulado")
        emitAll(products)
    }

    override suspend fun getProduct(id: Long): Product? = products.value.find { it.id == id }
}
