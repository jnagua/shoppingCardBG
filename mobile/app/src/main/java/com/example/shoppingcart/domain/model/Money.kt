package com.example.shoppingcart.domain.model

/**
 * Importe en centavos enteros de USD. Nunca es negativo.
 */
@JvmInline
value class Money(val cents: Long) {

    init {
        require(cents >= 0) { "Money no puede ser negativa: $cents" }
    }

    operator fun plus(other: Money): Money = Money(cents + other.cents)

    operator fun times(quantity: Quantity): Money = Money(cents * quantity.value)

    companion object {
        val ZERO = Money(0)
    }
}
