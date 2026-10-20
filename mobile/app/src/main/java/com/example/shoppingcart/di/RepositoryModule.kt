package com.example.shoppingcart.di

import com.example.shoppingcart.data.repository.CartRepositoryImpl
import com.example.shoppingcart.data.repository.ProductRepositoryImpl
import com.example.shoppingcart.domain.repository.CartRepository
import com.example.shoppingcart.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository
}
