package com.example.shoppingcart.di

import com.example.shoppingcart.data.SimulatedLatency
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataConfigModule {

    private const val SIMULATED_LATENCY_MS = 500L

    @Provides
    fun provideSimulatedLatency(): SimulatedLatency = SimulatedLatency(SIMULATED_LATENCY_MS)
}
