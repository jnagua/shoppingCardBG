package com.example.shoppingcart.domain.usecase

import com.example.shoppingcart.domain.model.Product
import com.example.shoppingcart.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> = productRepository.observeProducts()
}
