package com.example.shoppingcart.data.repository

import com.example.shoppingcart.data.SimulatedLatency
import com.example.shoppingcart.data.local.dao.ProductDao
import com.example.shoppingcart.data.mapper.toDomain
import com.example.shoppingcart.domain.model.Product
import com.example.shoppingcart.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val simulatedLatency: SimulatedLatency
) : ProductRepository {

    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeAll()
            .onStart { simulatedLatency() }
            .map { products -> products.map { it.toDomain() } }

    override suspend fun getProduct(id: Long): Product? =
        productDao.getById(id)?.toDomain()
}
