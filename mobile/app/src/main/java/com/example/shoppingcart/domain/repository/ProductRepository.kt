package com.example.shoppingcart.domain.repository

import com.example.shoppingcart.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun observeProducts(): Flow<List<Product>>

    suspend fun getProduct(id: Long): Product?
}
