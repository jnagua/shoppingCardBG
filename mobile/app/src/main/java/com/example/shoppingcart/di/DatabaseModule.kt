package com.example.shoppingcart.di

import android.content.Context
import androidx.room.Room
import com.example.shoppingcart.data.local.AppDatabase
import com.example.shoppingcart.data.local.dao.CartDao
import com.example.shoppingcart.data.local.dao.ProductDao
import com.example.shoppingcart.data.local.seed.ProductSeedCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
            .addCallback(ProductSeedCallback(context))
            .build()

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    fun provideCartDao(database: AppDatabase): CartDao = database.cartDao()
}
