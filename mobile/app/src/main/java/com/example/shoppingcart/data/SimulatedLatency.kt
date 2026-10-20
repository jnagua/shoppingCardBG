package com.example.shoppingcart.data

import kotlinx.coroutines.delay

/**
 * Retraso artificial que imita la latencia de una API remota, para que la UI
 * muestre estados de carga y bloquee envíos duplicados.
 */
class SimulatedLatency(private val millis: Long) {

    suspend operator fun invoke() {
        if (millis > 0) delay(millis)
    }
}
